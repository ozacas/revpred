package au.edu.unimelb.plantcell.hhblit;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.LogOutputStream;

import au.edu.unimelb.plantcell.onekp.eclipselink.HHPhredHit;
import au.edu.unimelb.plantcell.onekp.eclipselink.PDBHits;

/**
 * Responsible for extracting hits to parse an updating internal state to reflect that. Only
 * hits which pass the constructor-specified {@link AcceptHitCallback} are persisted to the database,
 * enabling hit filtering.
 * 
 * @author acassin
 *
 */
public class HHBlitsHitParser extends LogOutputStream {
	private boolean before_hit_summary, done_hits_summary;
	private final List<HHPhredHit> hits = new ArrayList<HHPhredHit>();
	private AcceptHitCallback acceptor = null;
	private PDBHits owner;
	
	public HHBlitsHitParser(final PDBHits owner, final AcceptHitCallback acceptor) {
		before_hit_summary = true;
		done_hits_summary = false;
		this.acceptor = acceptor;
		this.owner = owner;
		if (acceptor == null) {
			this.acceptor = new AcceptHitCallback() {

				@Override
				public boolean accept(HHPhredHit test_me) {
					return (test_me.getProbability() >= 50.0d);
				}
			};
		}
	}
	
	@Override
	protected void processLine(String line, int lvl) {
		String tline = line.trim();
		if (before_hit_summary) {
			if (tline.startsWith("No Hit ") && tline.endsWith("Template HMM")) {
				before_hit_summary = false;
			}
			return;
		}
		if (done_hits_summary) {
			return;
		}
		
		if (tline.equals("No 1")) {
			done_hits_summary = true; return;
		}
		String[] fields = tline.split("\\s+");
		HHPhredHit hit = new HHPhredHit();
		hit.setHitIndex(Integer.valueOf(fields[0]));
		hit.setPDBID(fields[1]);
		hit.setOwner(owner);
		// we process from the last argument to the first, since we cant know how many space-separated tokens
		// the hit description produced
		int last = fields.length-1;
		
		// if the previous field is short enough there will be spaces, but if not we remove the integer within parentheses from being considered
		if (fields[last].matches("\\(\\d+\\)")) {
			last--;
		} else if (fields[last].matches("\\d+-\\d+\\(\\d+\\)")) {
			fields[last] = fields[last].replaceFirst("\\(\\d+\\)$", "");
		}
		parseTemplateStartEnd(hit, fields[last--]);
		parseQueryStartEnd(hit, fields[last--]);
		hit.setCols(Integer.valueOf(fields[last--]));
		hit.setSS(Double.valueOf(fields[last--]));
		hit.setScore(Double.valueOf(fields[last--]));
		hit.setPvalue(Double.valueOf(fields[last--]));
		hit.setEvalue(Double.valueOf(fields[last--]));
		hit.setProbability(Double.valueOf(fields[last--]));
		
		if (acceptor.accept(hit)) {
			hits.add(hit);
		}
	}

	private void parseQueryStartEnd(final HHPhredHit hit, final String start_end) {
		assert(hit != null && start_end != null);
		String[] fields = start_end.trim().split("-");
		if (fields.length == 2) {
			hit.setQueryStart(Integer.valueOf(fields[0]));
			hit.setQueryEnd(Integer.valueOf(fields[1]));
		}
	}

	private void parseTemplateStartEnd(final HHPhredHit hit, final String start_end) {
		assert(hit != null && start_end != null);
		String[] fields = start_end.trim().split("-");
		if (fields.length == 2) {
			hit.setTemplateStart(Integer.valueOf(fields[0]));
			hit.setTemplateEnd(Integer.valueOf(fields[1]));
		}
	}

	public List<HHPhredHit> getHits() {
		return hits;
	}
}
