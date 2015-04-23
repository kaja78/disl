package org.disl.pattern;



public abstract class Pattern implements Executable {
	abstract Collection<Step> getSteps()
	
	@Override
	public void execute() {
		long timestamp=System.currentTimeMillis();
		println "Executing pattern $this:"
		steps.each {it.execute()}
		println "${this} executed in ${System.currentTimeMillis()-timestamp} ms"		
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
