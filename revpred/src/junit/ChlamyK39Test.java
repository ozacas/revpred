package junit;

import java.io.File;

import org.junit.Test;

import au.edu.unimelb.plantcell.hhpred.AcceptHitCallback;
import au.edu.unimelb.plantcell.jpa.hhpred.HHPhredHit;

/**
 * Tries to identify chlamy TPLATE complex proteins using the Ginkgo test as the superclass.
 * 
 * @author acassin
 */
public class ChlamyK39Test extends ChlamyTest {
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "revpred_chlamy_k39_minipdb";
	}
	
	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/test/chlamy_tplate_complex/hunt_for_ttray/UTRE.centroids.fasta");
	}
	
	/**
	 * To avoid populating the database with poor hits (filling up limited disk space) we require a minimum
	 * 10% threshold on the hit probability to persist it to the database
	 */
	@Override
	protected AcceptHitCallback getHitAcceptor() {
		return new AcceptHitCallback() {

			@Override
			public boolean accept(final HHPhredHit test_me) {
				return (test_me.getProbability() >= 10.0);
			}
		};
	}
	
	@Override
	@Test
	public void runTests() {
		populateDatabaseForTesting();
		
	}
}
