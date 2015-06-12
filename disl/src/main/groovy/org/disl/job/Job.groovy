/*
 * Copyright 2015 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
	}
	
	public synchronized void traceStatus() {
		println "********************************************************************************"
		jobEntries.each({println it})
		println "********************************************************************************"
	}
	
	static class JobEntry implements Executable {
		
		
		Executable executable
		Status status=Status.NEW
		long createdTime=System.currentTimeMillis()
		long startTime
		long finishTime
		Exception exception
		
		
		void execute() {
			try {
				status=Status.RUNNING
				startTime=System.currentTimeMillis()
				executable.execute()
				finishTime=System.currentTimeMillis()
				status=Status.FINISHED
			} catch (Exception e) {
				finishTime=System.currentTimeMillis()
				status=Status.ERROR
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
			String stat=status.toString().padLeft(10).toString().substring(0,10)
			return "* ${name} * ${stat} * ${dur} *"			
		}
		
		static enum Status {
			NEW,
			RUNNING,
			FINISHED,
			ERROR
		}
		
	}
	
	
}
