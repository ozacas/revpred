package au.edu.unimelb.plantcell.onekp.eclipselink;

public class JPAConstants {

	/*
	 * PDB entry ID SQL data type (used for JPA persistence)
	 */
	public final static String SQL_TYPE_FOR_PDB_ID = "CHAR(6)";
	
	/**
	 * 1kp identifier SQL data type (used for JPA persistence)
	 */
	public static final String SQL_TYPE_FOR_1KP_ID = "CHAR(16)";
	
	/**
	 * PDB entry descriptions are stored using this column type
	 */
	public static final String SQL_TYPE_FOR_PDB_DESCRIPTION = "TEXT";

	/**
	 * For storing protein sequence data
	 */
	public static final String SQL_TYPE_FOR_SEQUENCE_DATA = "TEXT";
}
