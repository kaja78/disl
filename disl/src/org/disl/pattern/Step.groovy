package org.disl.pattern;


public class Step implements Executable{	
	String name;
	def pattern='';
	
	String getCode() {
		if (getPattern() instanceof Closure) {
			getPattern().call();
		}
		getPattern();		
	}
	
	@Override
	public void simulate() {
		println "  Simulating step ${this}:"
		println """\t${code.replace("\n","\n\t")}""";		
	}
	
	@Override
	public void execute() {
		throw new UnsupportedOperationException();		
	}
	
	public String getName() {
		if (name==null) {
			return this.getClass().getSimpleName() 
		}
		return name
	}
	
	@Override
	public String toString() {
		return getName()
	}
}
