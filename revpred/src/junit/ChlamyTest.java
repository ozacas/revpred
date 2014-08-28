package junit;

import java.io.File;

import org.junit.Test;

public class ChlamyTest extends ArabidopsisTest {
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "revpred_chlamy_full_db";
	}
	
	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/test/chlamy_tplate_complex/chlamy_family_1kp_predicted_proteins.fasta");
	}
	
	@Override
	@Test
	public void runTests() {
		populateDatabaseForTesting();
		
	}
}
