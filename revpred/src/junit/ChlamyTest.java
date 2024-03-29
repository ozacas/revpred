package junit;

import java.io.File;

import org.junit.Test;

/**
 * Tries to identify chlamy TPLATE complex proteins using the Ginkgo test as the superclass.
 * 
 * @author acassin
 */
public class ChlamyTest extends MossTest {
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "revpred_chlamy_minipdb";
	}
	
	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/sequence-databases/phytozome.net/Creinhardtii_281_v5.5.protein.fa");
	}
	
	@Override
	@Test
	public void runTests() {
		populateDatabaseForTesting();
		
	}
}
