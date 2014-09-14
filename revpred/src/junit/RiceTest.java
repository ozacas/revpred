package junit;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author acassin
 *
 */
public class RiceTest extends ArabidopsisTest {	
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "revpred_rice_minipdb";
	}
	
	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/sequence-databases/phytozome.net/Osativa_204_protein.fa");
	}
	
	@Test
	public void runTests() {
		populateDatabaseForTesting();
		//testDatabaseForCorrectResults();
	}
}
