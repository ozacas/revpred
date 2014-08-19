package au.edu.unimelb.plantcell.onekp.eclipselink;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Represents a single sequences from 1kp: the sequence ID must be of the form SAMPLEID_NUMBER where SAMPLEID = AAAA .. ZZZZ
 * and number is a positive integer representing the contig index within the transcriptome assembly for that sample
 * 
 * @author acassin
 *
 */
@Entity
@Table(name="Sequence")
public class Sequence implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3082149716995606228L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	// sequence id: always of the form [A-Z]{4}_\d+
	@Column(columnDefinition=JPAConstants.SQL_TYPE_FOR_1KP_ID)
	private String seq_id;
	
	// protein/nucleotide sequence from 1kp assembly
	@Column(columnDefinition=JPAConstants.SQL_TYPE_FOR_SEQUENCE_DATA)
	private String seq;
	@Transient
	private int length;			// not stored in db since it is implicit via seq
	
	public String getID() { return seq_id; }
	
	public void setID(String new_id) {
		setID(new_id, false);
	}
	
	public void setID(String new_id, boolean strict) throws IllegalArgumentException {
		if (new_id == null || (strict && !new_id.matches("^[A-Z]{4}_\\d+$"))) {
			throw new IllegalArgumentException("Valid 1kp assembly sequence identifier required!");
		}
		seq_id = new_id;
	}
	
	public String getSequence() { return seq; }
	
	public void setSequence(String new_seq) { seq = new_seq; }
	
	public int getLength() {
		return (seq !=null) ? seq.length() : 0;
	}
}
