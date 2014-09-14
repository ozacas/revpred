package au.edu.unimelb.plantcell.jpa.hhpred;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.eclipse.persistence.annotations.Index;

/**
 * Represents a single PDB hit as found by hhblits against the PDB70 database. The query sequence which 
 * created this hit is not kept in this table - it is up to the program to correctly create the link to the <code>HHPhredHit.id</code>
 * 
 * @author acassin
 *
 */
@Entity
public class HHPhredHit implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3329719905417474999L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	// hit number index (1=tophit, 2=next best, 3...)
	@Column(name="HitIndex")		// TO AVOID SQL Syntax clash with keyword
	private int index;
	
	// owner (that gave rise to the hit)
	private PDBHits pdb_hits;
	
	// hit from HHPhred to this particular PDB database entry (format is controlled)
	@Column(columnDefinition=JPAConstants.SQL_TYPE_FOR_PDB_ID)
	@Index
	private String pdb_id;
	
	private double score, e_value, p_value, ss, probability;	// match scores as computed by HHBlits
	private int cols;
	
	private int query_start, query_end;			// Query HMM match extent
	private int template_start, template_end;	// Template HMM match extent
	
	public String getPDBID() { return pdb_id; }
	public void setPDBID(String new_id) { pdb_id = new_id; }
	
	public double getScore() { return score; }
	public void setScore(double new_score) { score = new_score; }
	
	public double getEvalue() { return e_value; }
	public void setEvalue(double new_evalue) { e_value = new_evalue; }
	
	public double getPvalue() { return p_value; }
	public void setPvalue(double new_pvalue) { p_value = new_pvalue; }
	
	public double getSS() { return ss; }
	public void setSS(double new_ss) { ss = new_ss; }
	
	public int getQueryStart()                  { return query_start; }
	public int getQueryEnd()                    { return query_end; }
	public void setQueryStart(int new_start)    { query_start = new_start; }
	public void setQueryEnd(int new_end)        { query_end = new_end; }
	
	public int getTemplateStart()               { return template_start; }
	public int getTemplateEnd()                 { return template_end; }
	public void setTemplateStart(int new_start) { template_start = new_start; }
	public void setTemplateEnd(int new_end)     { template_end = new_end; }
	
	public int getCols() { return cols; }
	public void setCols(int new_cols) { cols = new_cols; }
	
	public int getHitIndex() { return index; }
	public void setHitIndex(int new_index) { index = new_index; }
	
	public void setProbability(double new_prob) { probability = new_prob; }
	public double getProbability() { return probability; }
	
	public void setOwner(final PDBHits hits) { pdb_hits = hits; }
	public PDBHits getOwner() { return pdb_hits; }
}
