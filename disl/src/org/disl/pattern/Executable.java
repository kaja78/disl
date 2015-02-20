package org.disl.pattern;


public interface Executable {

	/**
	 * Execute given element.
	 * */
	public abstract void execute();

	/**
	 * Simulate execution of given element. Simulation
	 * means tracing output without execution.
	 */
	public abstract void simulate();

}