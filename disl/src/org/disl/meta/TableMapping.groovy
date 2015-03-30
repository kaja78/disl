package org.disl.meta

import org.disl.pattern.Pattern;

abstract class TableMapping extends Mapping{
	public abstract Table getTarget()
	public abstract Pattern getPattern()
	
	public void simulate() {
		getPattern().simulate()
	}
	
	public void execute() {
		getPattern().execute()
	}

}
