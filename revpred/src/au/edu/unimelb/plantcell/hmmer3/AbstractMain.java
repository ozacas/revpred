package au.edu.unimelb.plantcell.hmmer3;

import java.io.File;
import java.io.IOException;

public class AbstractMain {

	public String getHmmerProgram() {
		return "/usr/bin/hmmsearch";
	}
	
	public String getHmmBuildProgram() {
		return "/usr/bin/hmmbuild";
	}
	
	protected void makeHmm(final File alignment_file) throws IOException {
		
	}
}
