/*
 * Copyright 2015 - 2016 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.workflow

import org.disl.meta.MetaFactory
import org.disl.pattern.AbstractExecutable
import org.disl.pattern.Executable

import groovy.transform.CompileStatic;
import groovy.util.logging.Slf4j;
/**
 * Job executes list of job entries in serial order.
 * */
@Slf4j
@CompileStatic
abstract class Job extends AbstractExecutable {

	List<JobEntry> jobEntries=[]

	/**
	 * Add executable instance to job entry list.
	 * */
	void add(Executable executable) {
		this.jobEntries.add(new JobEntry(executable: executable))
	}

	/**
	 * Create new instance of Executable and add it to job entry list.
	 * */
	public Job addType(Class<? extends Executable> type) {
		add((Executable)MetaFactory.create(type))
		return this
	}

	/**
	 * Create list of Executable instances and add it to job entry list.
	 * */
	public Job addTypes(List<Class<? extends Executable>> types) {
		types.each({ addType(it) })
		return this
	}

	/**
	 * Add list of executables to job entry list.
	 * */

	public Job addAll(List<? extends Executable> executables) {
		executables.each { add(it) }
		return this
	}

	/**
	 * Find, create and add executables in job package and all subpackages to job entry list. 
	 * Compiled executables must be located in the same classpath element (directory or jar).
	 * @param assignableType Only classes assignable from assignableType will be added to job entry list.
	 * */
	public Job addAll(Class assignableType) {
		addAll(MetaFactory.createAll(this.getClass(),this.getClass().getPackage().getName(),assignableType));
	}

	/**
	 * Find, create and add executables to job entry list. 
	 * Compiled executables must be located in the same classpath element (directory or jar). 
	 * @param rootPackage Root package to look for executables classes in.
	 * @param assignableType Only classes assignable from assignableType will be added to job entry list.
	 * */
	public Job addAll(String rootPackage,Class assignableType) {
		addAll(MetaFactory.createAll(this.getClass(),rootPackage,assignableType));
	}

	/**
	 * Find, create and add executables to job entry list.
	 * @param traversePath Root path to look for executables classes in.
	 * @param rootPackage Root package to look for executables classes in.
	 * @param assignableType Only classes assignable from assignableType will be added to job entry list.
	 * */
	@Deprecated
	public Job addAll(String traversePath,String rootPackage,Class assignableType) {
		addAll(MetaFactory.createAll(traversePath,rootPackage,assignableType));
	}

	protected int executeInternal() {
		int processedRows=0
		jobEntries.each {
			it.execute(); processedRows+=it.executionInfo.processedRows
		}
		return processedRows
	}

	@Override
	public void postExecute() {
		super.postExecute();
		traceStatus()
	}

	public void simulate() {
		jobEntries.each { it.simulate() }
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()
	}

	
	public void traceStatus() {
		String name=toString().padRight(50).toString().substring(0,50)
		String dur=executionInfo.duration.toString().padLeft(10).toString().substring(0,10)
		String stat=executionInfo.status.toString().padLeft(10).toString().substring(0,10)
		String processedRows=executionInfo.processedRows.toString().padLeft(10).toString().substring(0,10)

		if (log.infoEnabled) {
			String message=""" Execution results for ${name}:
*********************************************************************************************
*  Name                                              *   Status   *  Time (ms)*        Rows *
*********************************************************************************************
* ${name} * ${stat} * ${dur} * ${processedRows} *
*********************************************************************************************
${jobEntries.join('\n')}
*********************************************************************************************
"""
				log.info(message)
		}
	}
}
