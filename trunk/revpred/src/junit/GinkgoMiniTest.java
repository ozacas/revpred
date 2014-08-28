package junit;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import au.edu.unimelb.plantcell.hhblit.AcceptHitCallback;
import au.edu.unimelb.plantcell.onekp.eclipselink.HHPhredHit;

/**
 * This test populates the revpred_ginkgo_mini_db database as described in persistence.xml with
 * only hits relating to approximately 20 PDB models from a mini-pdb database. This keeps the performance
 * up (not storing entries not of interest) and database size down. These results will be used
 * by JohnH to investigate improvements to revpred to identify suitable homologs to the TSET complex.
 * 
 * This program does not run the tests in the superclass.
 * 
 * @author acassin
 *
 */
public class GinkgoMiniTest extends ArabidopsisTest {
	
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "revpred_ginkgo_mini_db";
	}

	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/test/tplate-complex-arabidopsis/SGTW.fasta");
	}
	
	@Override
	public String getPDBDatabase() {
		return "tplate_hhm_db";
	}

	@Override
	public File getPDBDatabaseFolder() {
		return new File("/home/acassin/minipdb");
	}
	
	@Override
	@Test
	public void runTests() {
		populateDatabaseForTesting();
	}
}
