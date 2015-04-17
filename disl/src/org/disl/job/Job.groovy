package org.disl.job

import org.disl.meta.MetaFactory
import org.disl.pattern.Executable
import org.disl.pattern.Pattern

class Job implements Executable {

	List<Executable> jobEntries=[]

	public Job addType(Class<Executable> type) {
		this.jobEntries.add(MetaFactory.create(type))
		this
	}
	
	public Job addAll(List<Executable> jobEntries) {
		this.jobEntries.addAll(jobEntries)
		this
	}

	public Job addAll(String traversePath,String rootPackage,Class assignableType) {
		addAll(MetaFactory.createAll(traversePath,rootPackage,assignableType));
	}

	public void execute() {
		jobEntries.each {it.execute()}
	}

	public void simulate() {
		jobEntries.each {it.simulate()}
	}
}
