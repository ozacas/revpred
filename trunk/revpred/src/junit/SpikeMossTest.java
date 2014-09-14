package junit;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author acassin
 *
 */
public class SpikeMossTest extends ArabidopsisTest {	
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "revpred_spikemoss_minipdb";
	}
	
	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/sequence-databases/phytozome.net/Smoellendorffii_91_peptide.fa");
	}
	
	@Test
	public void runTests() {
		populateDatabaseForTesting();
		//testDatabaseForCorrectResults();
	}
}
