package au.edu.unimelb.plantcell.jpa.hhpred;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.Index;

/**
 * Represents a list of keywords associated with a given PDB database entry. Currently just
 * derived from the description, but other parts of the database may be used in future eg. FAM
 * 
 * @author acassin
 *
 */
@Entity
public class Keyword {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private PDBEntry owner;
	
	@Index
	private String keyword;
	
	public void setKeyword(String new_keyword) { keyword = new_keyword; }
	public String getKeyword() { return keyword; }
	
	public void setOwner(PDBEntry new_owner) { owner = new_owner; }
	public PDBEntry getOwner() { return owner; }
}
