package org.disl.pattern;


public class Step implements Executable{	
	String name;
	def pattern;
	
	String getCode() {
		if (pattern instanceof Closure) {
			pattern.call();
		}
		pattern;		
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
	
	@Override
	public String toString() {
		return "$name (${this.getClass().getSimpleName()})";
	}
}
