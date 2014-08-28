package au.edu.unimelb.plantcell.hhblit;

import au.edu.unimelb.plantcell.onekp.eclipselink.HHPhredHit;

public interface AcceptHitCallback {
	/**
	 * Returns true if the specified hit is to be persisted to the database, false otherwise.
	 * 
	 * @param test_me careful accessing state as not all of it has been initialised at the time of the call
	 * @return true if acceptable, false otherwise
	 */
	public boolean accept(final HHPhredHit test_me);
}
