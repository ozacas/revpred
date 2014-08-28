package junit;

import java.io.File;

public class ArabidopsisFullPDBTest extends ArabidopsisTest {

	/**
	 * Must correspond to name of the persistence unit in persistence.xml
	 * @return
	 */
	@Override
	protected String getPersistenceUnit() {
		return "revpred_at_full_db";
	}
	
	@Override
	public String getPDBDatabase() {
		return "pdb70_31Jul14_hhm_db";
	}

	@Override
	public File getPDBDatabaseFolder() {
		return new File("/home/acassin/pdb");
	}

}
