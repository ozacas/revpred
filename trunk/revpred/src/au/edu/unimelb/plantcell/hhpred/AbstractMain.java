package au.edu.unimelb.plantcell.hhpred;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

import au.edu.unimelb.plantcell.jpa.hhpred.HHPhredHit;
import au.edu.unimelb.plantcell.jpa.hhpred.PDBEntry;
import au.edu.unimelb.plantcell.jpa.hhpred.PDBHits;
import au.edu.unimelb.plantcell.jpa.hhpred.Sequence;

public abstract class AbstractMain {

	/**
	 * Return the full pathname to the hhsearch binary
	 * @return must not be null
	 */
	public abstract String getHHSearchProgram();
	
	/**
	 * Returns the database name
	 * @return must not be null and must end with "_hhm_db"
	 */
	public abstract String getPDBDatabase();
	
	/**
	 * Returns the non-null File instance representing the folder (directory) in which the PDB database resides
	 * @return must not be null
	 */
	public abstract File getPDBDatabaseFolder();
	
	/**
	 * Returns the EntityManagerFactory (singleton) instance used to persist data to the appropriate database.
	 * Override this method if you wish to change the database, and adding appropriate configuration to persistence.xml
	 */
	public abstract EntityManagerFactory getEntityManagerFactory();
	
	/**
	 * Returns an EntityManager used to persist objects. The instance is created using the factory as returned from
	 * <code>getEntityManagerFactory()</code>
	 */
	public abstract EntityManager getEntityManager();
	
	/**
	 * Read query sequences (must be protein) from the specified file. 
	 * @return must not be null
	 */
	public abstract File getQueryFastaFile();
	
	/**
	 * The default implementation accepts all hits for storage into the database. But if you
	 * wish to filter so that only some hits are accepted (eg. threshold) then override this
	 * with your own callback
	 */
	protected AcceptHitCallback getHitAcceptor() {
		return new AcceptHitCallback() {

			@Override
			public boolean accept(final HHPhredHit test_me) {
				return true;
			}
			
		};
	}

	
	/*********************************************
	 * METHODS USED BY ALL SUBCLASSES
	 *********************************************/
	

	/**
	 * Add description for each PDB database entry to configured database
	 */
	protected void populatePDBEntries(final EntityManager em, File hhsearch_formatted_file) throws IOException,ParseException {
		assert(em != null && hhsearch_formatted_file != null);
		
		BufferedReader rdr = new BufferedReader(new FileReader(hhsearch_formatted_file));
		int seen = 0;
		int accepted = 0;
		
		try {
			String line;
			PDBEntry ret = new PDBEntry();
			while ((line = rdr.readLine()) != null) {
				if (line.startsWith("NAME "))  {
					String descr = line.substring("NAME ".length()).replaceFirst("^[\\x00-\\x200\\xA0]+", "").replaceFirst("[\\x00-\\x20\\xA0]+$", "");  ;
					int pos = descr.indexOf(' ');
					if (pos < 0) {
						throw new IOException("No PDB ID provided!");
					}
					ret.setPDBID(descr.substring(0, pos));
					descr = descr.substring(pos+1);
					ret.setDescription(descr);
					ArrayList<String> k = new ArrayList<String>();
					String[] keywords = descr.split("\\s*;\\s*");
					for (String s : keywords) {
						k.add(s);
					}
					ret.setKeywords(ret, k);
					seen++;
				} else if (line.startsWith("FAM ")) {
					// current database does not use this field... so we ignore for now....
				} else if (line.startsWith("DATE ")) {
					String date = line.substring("DATE ".length());
					// eg. DATE  Thu Jan 30 15:59:01 2014
					Date legacy_date = new SimpleDateFormat("EEE MMM dd HH:mm:ss YYYY").parse(date.trim());
					ret.setCreationDate(legacy_date);
				} else if (line.startsWith("LENG ")) {
					// eg. LENG  63 match states, 63 columns in multiple alignment
					Pattern p = Pattern.compile("^LENG\\s+(\\d+) match states, (\\d+) columns in multiple alignment$");
					Matcher m = p.matcher(line);
					if (!m.matches()) {
						throw new IOException("Unknown/supported LENG line: "+line);
					}
					ret.setMatchStates(Integer.valueOf(m.group(1)));
					ret.setSites(Integer.valueOf(m.group(2)));
				} else if (line.startsWith("NEFF ")) {
					ret.setNEFF(Double.valueOf(line.substring("NEFF ".length()).trim()));
				} else if (line.equals("//")) {
					if (ret.isValid()) {
						// persist...
						accepted++;
						em.persist(ret);
						System.err.println("Persisted "+ret.getPDBID());
						// and start new entry
						ret = new PDBEntry();
					} else {
						throw new IOException("bogus PDB Entry: "+ret);
					}
				}
			}
		} finally {
			System.err.println("Processed "+seen+" database entries from "+hhsearch_formatted_file.getAbsolutePath());
			System.err.println("Accepted "+accepted+" database entries (saved to database)");
			rdr.close();
		}
	}

	/**
	 * Run HHBlits for the specified FASTA sequences, in turn, against the configured PDB database.
	 * Store hits in the database as required. Sequences without any PDB hits are not stored in the database.
	 * 
	 * @throws IOException
	 */
	protected void runHHPhred(final EntityManager em, final File query_fasta_file) throws IOException {
		CommandLine phred = new CommandLine(getHHSearchProgram());
		phred.addArgument("-i");
		File query_fasta_sequence_file = File.createTempFile("single_seq", "_query.fasta");
		phred.addArgument(query_fasta_sequence_file.getAbsolutePath());
		phred.addArgument("-d");
		phred.addArgument(getPDBDatabase());
		
		DefaultExecutor exe = new DefaultExecutor();
		exe.setWorkingDirectory(getPDBDatabaseFolder());
		exe.setExitValue(0);
		
		BufferedReader rdr = new BufferedReader(new FileReader(query_fasta_file));
		String seq_id = null;
		int done = 0;
		int persisted= 0;
		long start = System.currentTimeMillis();
		do {
			StringBuilder sb = new StringBuilder(10 * 1024);
			seq_id = readNextSequence(rdr, sb);
			done++;
			if (seq_id != null && sb.length() > 0) {
				System.err.println("Saving "+seq_id+" to "+query_fasta_sequence_file.getAbsolutePath());
				saveQuerySequenceTo(query_fasta_sequence_file, seq_id, sb.toString());
				Sequence query = new Sequence();
				query.setID(seq_id);
				query.setSequence(sb.toString());
				PDBHits hit = new PDBHits();
				hit.setQuerySequence(query);
				HHBlitsHitParser parser = new HHBlitsHitParser(hit, getHitAcceptor());
				LogOutputStream stderr_logger = new LogOutputStream() {

					@Override
					protected void processLine(String line, int count) {
						// just ignore error lines for now: will be handled using the exit status alone for now
					}
					
				};
				exe.setStreamHandler(new PumpStreamHandler(parser, stderr_logger));
				System.err.println("Running: "+phred.toString());
				long before_run = System.currentTimeMillis();
				int result = exe.execute(phred);
				long after_run = System.currentTimeMillis();
				System.err.println("Took "+((after_run-before_run)/1000)+" seconds for "+seq_id);
				
				if (exe.isFailure(result)) {
					throw new IOException("Unable to run "+phred);
				}
				List<HHPhredHit> hits = parser.getHits();
				System.err.println("Got "+hits.size()+" hits for "+seq_id);
				hit.setHits(hits);
				boolean got_first = false;
				for (HHPhredHit s : hits) { 
					if (!got_first) {
						System.err.println("Top hit for "+seq_id+": "+s.getPDBID());
						got_first = true;
					}
				}
				
				if (hit.getHits().size() > 0) {
					System.err.println("Persisting "+seq_id+" and associated hits.");
					EntityTransaction t = em.getTransaction();
					try {
						t.begin();
						em.persist(hit);
						t.commit();
						persisted++;
					} catch (Exception e) {
						if (t.isActive()) {
							t.rollback();
						}
						throw e;
					}
				} else {
					System.err.println("No hits for "+seq_id);
				}
			}
			
			query_fasta_sequence_file.delete();
		} while (seq_id != null);
		
		long end = System.currentTimeMillis();
		System.err.println("Read "+done+" OneKP protein sequences.");
		System.err.println("Persisted hits for "+persisted+" query sequences.");
		System.err.println("Time taken: "+(int)((end-start)/1000)+" seconds.");
	}

	/**
	 * Reads a single sequence into the specified StringBuilder, using <code>sb.append()</code> methods.
	 * 
	 * @param rdr
	 * @param sb
	 * @return
	 * @throws IOException
	 */
	protected String readNextSequence(final BufferedReader rdr, final StringBuilder sb) throws IOException {
		assert(rdr != null && sb != null && rdr.markSupported() && sb.length() == 0);
		String line = rdr.readLine();
		if (line == null) {
			return null;
		}
		if (!line.startsWith(">")) {
			throw new IOException("Expected sequence identifier!");
		}
		String id = line.substring(">".length()).trim();
		rdr.mark(10000);
		while ((line = rdr.readLine()) != null) {
			if (line.startsWith(">")) {
				rdr.reset();
				break;			// current sequence definition has ended so...
			}
			rdr.mark(10000);
			sb.append(line.trim());
		}
		return id;
	}

	/**
	 * Saves a single (protein) sequence to the specified file. The file is deleted before it is created.
	 * The sequence is given the specified id.
	 * 
	 * @param query_fasta_sequence_file file to save the protein sequence to
	 * @param id must not be null
	 * @param seq must not be null
	 * @throws IOException on fatal error
	 */
	protected void saveQuerySequenceTo(File query_fasta_sequence_file, String id, String seq) throws IOException {
		if (id == null || seq == null || seq.length() < 1) {
			throw new IOException("Illegal arguments to save to query file! length="+seq.length()+" id="+id);
		}
		query_fasta_sequence_file.delete();
		PrintWriter pw = new PrintWriter(query_fasta_sequence_file);
		pw.println(">"+id);
		pw.println(seq);
		pw.close();
	}
}
