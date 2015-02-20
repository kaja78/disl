package org.disl.pattern;



public abstract class Pattern implements Executable {
	abstract List<Step> getSteps()
	
	@Override
	public void execute() {
		println "Executing pattern $this:"
		steps.each {it.execute()}		
	}
	
	@Override
	public void simulate() {
		println "Simulating pattern $this:"
		steps.each {it.simulate()}		
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
