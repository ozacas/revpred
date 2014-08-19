package au.edu.unimelb.plantcell.hhblit;

import java.io.File;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main extends AbstractMain {
	private EntityManagerFactory singleton = null;
	
	/*private final static String HHSEARCH = "/usr/bin/hhsearch";
	private final static String PDB_FOLDER = "/home/acassin/minipdb";
	private final static String PDB_DATABASE_PREFIX = "minipdb";
	private final static String ONEKP_PROTEIN_FASTA = "/home/acassin/test/tplate-complex-arabidopsis/SGTW.fasta";
	//private final static String ONEKP_PROTEIN_FASTA = "/home/acassin/combined_1kp_predicted_proteins_2013.fasta";
	//private final static String ONEKP_PROTEIN_FASTA = "/home/acassin/sequence-databases/siria_rice_pm_proteins_20121128.fasta";
	private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("onekpdb");
	*/
	
	
	public static void main(String[] args) {
		EntityTransaction t = null;
		Main m = new Main();
		try {
			File pdb_folder = m.getPDBDatabaseFolder();
			if (!pdb_folder.exists()) {
				throw new IOException("No such folder: "+m.getPDBDatabaseFolder());
			}
			EntityManager em = m.getEntityManager();

			// read database file to load keyword database
			System.err.println("Populating PDB Entries....");
			t = em.getTransaction();
			t.begin();
			m.populatePDBEntries(em, new File(pdb_folder, m.getPDBDatabase()));
			t.commit();
			t = null;
			System.err.println("After population of PDB data entries");
			
			// run hhpred and populate the database with hits and sequences. Sequences without
			// hits do not appear in the database
			m.runHHPhred(em, m.getQueryFastaFile());
			
			// all done
			System.exit(0);
		} catch (Exception e) {
			if (t != null) {
				t.rollback();
			}
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public String getHHSearchProgram() {
		return "/usr/bin/hhsearch";
	}

	@Override
	public String getPDBDatabase() {
		return "minipdb";
	}

	@Override
	public File getPDBDatabaseFolder() {
		return new File("/home/acassin/minipdb");
	}

	private String getPersistenceUnit() {
		return "onekpdb";
	}
	
	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		if (singleton == null) {
			singleton = Persistence.createEntityManagerFactory(getPersistenceUnit());
		}
		return singleton;
	}

	@Override
	public EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/combined_1kp_predicted_proteins_2013.fasta");
	}
}
