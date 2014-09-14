package junit;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author acassin
 *
 */
public class BrachyTest extends ArabidopsisTest {	
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "revpred_bd_minipdb";
	}
	
	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/sequence-databases/phytozome brachypodium jgi datasets/Bdistachyon_114_peptide.fa");
	}
	
	@Test
	public void runTests() {
		populateDatabaseForTesting();
		testDatabaseForCorrectResults();
	}
}
