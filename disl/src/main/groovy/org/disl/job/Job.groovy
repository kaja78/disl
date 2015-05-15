package org.disl.job

import org.disl.meta.MetaFactory
import org.disl.pattern.Executable
import org.disl.pattern.Pattern

class Job implements Executable {

	List<JobEntry> jobEntries=[]
	
	void add(Executable executable) {
		this.jobEntries.add(new JobEntry(executable: executable))
	}

	public Job addType(Class<Executable> type) {
		add(MetaFactory.create(type))
		return this
	}
	
	public Job addTypes(List<Class<Executable>> types) {
		types.each({addType(it)})
		return this
	}
	
	public Job addAll(List<Executable> executables) {
		executables.each {add(it)}
		return this
	}

	public Job addAll(String traversePath,String rootPackage,Class assignableType) {
		addAll(MetaFactory.createAll(traversePath,rootPackage,assignableType));
	}

	public void execute() {
		try {
			jobEntries.each {it.execute()}
		} finally {
			traceStatus()			
		}
		
	}

	public void simulate() {
		jobEntries.each {it.simulate()}
	}
	
	public synchronized void traceStatus() {
		println "*****************************************************************"
		jobEntries.each({println it})
		println "*****************************************************************"
	}
	
	static class JobEntry implements Executable {
		static final int NEW=0
		static final int RUNNING=1
		static final int FINISHED=2
		static final int ERROR=3
		
		Executable executable
		int status=NEW
		long createdTime=System.currentTimeMillis()
		long startTime
		long finishTime
		Exception exception
		
		
		void execute() {
			try {
				status=RUNNING
				startTime=System.currentTimeMillis()
				executable.execute()
				finishTime=System.currentTimeMillis()
				status=FINISHED
			} catch (Exception e) {
				finishTime=System.currentTimeMillis()
				status=ERROR
				exception=e
				throw e
			}
		}
		
		void simulate() {
			executable.simulate()
		}
		
		int getDuration() {
			return finishTime-startTime
		}
		
		String toString() {
			String name=executable.toString().padRight(50).toString().substring(0,50)
			String dur=duration.toString().padLeft(10).toString().substring(0,10)
			return "* ${name} * ${status} * ${duration} *"			
		}
		
	}
}
