package au.edu.unimelb.plantcell.onekp.eclipselink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.Index;

/**
 * Represents a description of a single PDB database entry as read from the relevant files from the HHPhred download eg. .hmm files
 * 
 * Index is by both PDB ID and description so that particular keywords can be found quickly as this will be a key user-desired search.
 * 
 * @author acassin
 *
 */
@Entity
public class PDBEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5492374470783537374L;

	@Id
	@GeneratedValue
	private int id;
	
	@Column(columnDefinition=JPAConstants.SQL_TYPE_FOR_PDB_ID)
	@Index
	private String pdb_id;		// name
	
	@Column(columnDefinition=JPAConstants.SQL_TYPE_FOR_PDB_DESCRIPTION)
	private String description;	// as defined in name line of PDB HHPhred database (ie. all but pdb_id from that line)
	
	@Temporal(TemporalType.DATE)
	private Date creation_date;

	@OneToMany(cascade=CascadeType.PERSIST)
	@CascadeOnDelete
	private List<Keyword> keywords;
	
	private int sites, match_states;
	
	private double neff;		// NEFF == number of effective sequences
	
	
	public List<Keyword> getKeywords() { return keywords; }
	public void setKeywords(List<Keyword> new_keywords) { keywords = new_keywords; }
	public void setKeywords(PDBEntry owner, List<String> keywords) {
		ArrayList<Keyword> k = new ArrayList<Keyword>();
		for (String s : keywords) {
			Keyword kw = new Keyword();
			kw.setOwner(owner);
			kw.setKeyword(s);
			k.add(kw);
		}
		this.setKeywords(k);
	}
	
	public void setPDBID(String new_id) { pdb_id = new_id; };
	public String getPDBID() { return pdb_id; }
	
	public String getDescription() { return description; }
	public void setDescription(String descr) { 
		description = descr;
	}
	
	public Date getCreationDate() { return (creation_date == null) ? new Date() : creation_date; };
	
	public void setCreationDate(final Date new_date) { creation_date = new_date; }
	
	public void setSites(int new_sites) { sites = new_sites; }
	public int getSites() { return sites; }
	
	public int getMatchStates() { return match_states; }
	public void setMatchStates(int new_states) { match_states = new_states; }
	
	public double getNEFF() { return neff; }
	public void setNEFF(double new_neff) { neff = new_neff; }
	
	public boolean isValid() {
		if (pdb_id == null || pdb_id.length() < 1) {
			return false;
		}
		if (getMatchStates() < 1 || getSites() < 1) {
			return false;
		}
		if (getNEFF() <= 0.0d) {
			return false;
		}
		String descr = getDescription();
		if (descr == null || descr.length() < 1) {
			return false;
		}
		if (descr.length() > 250) {
			System.err.println("Very long description for "+pdb_id+" "+descr.length()+ " truncating to 250...");
			descr = descr.substring(0,250)+"...";
		}
		return true;
	}
}
