package org.disl.meta

import org.disl.pattern.Executable;
import org.disl.pattern.Pattern;

abstract class TableMapping extends Mapping implements Executable {
	public abstract Table getTarget()
	public abstract Pattern getPattern()
	
	public void simulate() {
		getPattern().simulate()
	}
	
	public void execute() {
		getPattern().execute()
	}

}
