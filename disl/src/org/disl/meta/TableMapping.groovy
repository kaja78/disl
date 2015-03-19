package org.disl.meta

import org.disl.pattern.Pattern;

abstract class TableMapping extends Mapping{
	abstract Table getTarget()
	abstract Pattern getPattern()
	
	public void simulate() {
		getPattern().simulate()
	}
	
	public void execute() {
		getPattern().execute()
	}

}
