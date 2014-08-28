package junit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Test;

import au.edu.unimelb.plantcell.hhblit.AbstractMain;

/**
 * 
 * @author acassin
 *
 */
public class ArabidopsisTest extends AbstractMain {
	private EntityManagerFactory singleton = null;
	
	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	protected String getPersistenceUnit() {
		return "ArabidopsisOneKPtestdb";
	}
	
	@Override
	public String getHHSearchProgram() {
		return "/usr/bin/hhsearch";
	}

	@Override
	public File getQueryFastaFile() {
		return new File("/home/acassin/test/tplate-complex-arabidopsis/test.fasta");
	}
	
	@Override
	public String getPDBDatabase() {
		return "minipdb_hhm_db";
	}

	@Override
	public File getPDBDatabaseFolder() {
		return new File("/home/acassin/minipdb");
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

	protected void populateDatabaseForTesting() {
		EntityTransaction t = null;
		try {
			File pdb_folder = getPDBDatabaseFolder();
			assertNotNull(pdb_folder);
			assertTrue(pdb_folder.exists());
			EntityManager em = getEntityManager();
			assertNotNull(em);

			// read database file to load keyword database
			/*System.err.println("Populating PDB Entries....");
			t = em.getTransaction();
			assertNotNull(t);
			t.begin();
			populatePDBEntries(em, new File(pdb_folder, getPDBDatabase()));
			t.commit();
			t = null;
			System.err.println("After population of PDB data entries");*/
			
			// run hhpred and populate the database with hits and sequences. Sequences without
			// hits do not appear in the database
			runHHPhred(em, getQueryFastaFile());
		} catch (Exception e) {
			if (t != null) {
				t.rollback();
			}
			e.printStackTrace();
			fail("No exception should be thrown during database population!");
		}
	}
	
	@SuppressWarnings("unused")
	private void testDatabaseForCorrectResults() {
		EntityManager em = getEntityManager();
		String db = getPersistenceUnit();
		String sql = "select t1.*,t2.*,t3.* from "+
				db+".Sequence as t1, "+db+".PDBHITS as t2,"+
				db+".HHPHREDHIT as t3 "+
			"where (t1.ID = t2.query_id AND "+
					"t2.pdb_query_id = t3.PDB_HITS_pdb_query_id AND "+
					"t3.PROBABILITY >= 90 AND "+
					"t3.PDB_ID in ('4cid_A', '3tjz_C', '2rqr_A', '4ikn_A', '4uqi_B', '3mkr_B', '3mkq_A'));";

		Query q = em.createQuery(sql).setMaxResults(1000);
		assertNotNull(q);
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.getResultList();
		assertNotNull(results);
		assertTrue(results.size() > 0);
		for (Object[] row : results) {
			
		}
	}
	
	@Test
	public void runTests() {
		populateDatabaseForTesting();
		testDatabaseForCorrectResults();
	}
}
