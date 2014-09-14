package au.edu.unimelb.plantcell.jpa.hhpred;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * Represent the set of hits from PDB (hhblits) for a given query sequence
 * @author acassin
 *
 */
@Entity
public class PDBHits implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 63232895996303244L;

	@Id
	@Column(name="pdb_query_id")
	@GeneratedValue
	private Long id;
	
	@OneToOne(cascade=CascadeType.PERSIST)
	private Sequence query;
	
	@OneToMany(mappedBy="pdb_hits", cascade=CascadeType.PERSIST)
	@CascadeOnDelete
	private List<HHPhredHit> hits;
	
	public void setQuerySequence(final Sequence s) { query = s; }
	public Sequence getQuerySequence() { return query; }
	
	public List<HHPhredHit> getHits() { return hits; }
	public void setHits(final List<HHPhredHit> new_hits) { hits = new_hits; }
	
}
