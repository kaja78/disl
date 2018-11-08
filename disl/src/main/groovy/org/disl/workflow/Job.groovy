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
import groovy.util.logging.Slf4j
import org.disl.pattern.Status

/**
 * Job executes list of job entries in serial order.
 * */
@Slf4j
@CompileStatic
abstract class Job extends AbstractExecutable {

	List<JobEntry> jobEntries=[]
	Exception lastException
	boolean continueOnError=false

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
		addAll(MetaFactory.createAll(this.getClass().getPackage().getName(),assignableType));
	}

	/**
	 * Find, create and add executables to job entry list. 
	 * Compiled executables must be located in the same classpath element (directory or jar). 
	 * @param rootPackage Root package to look for executables classes in.
	 * @param assignableType Only classes assignable from assignableType will be added to job entry list.
	 * */
	public Job addAll(String rootPackage,Class assignableType) {
		addAll(MetaFactory.createAll(rootPackage,assignableType));
	}

    @Override
    void beforeExecute() {
        super.beforeExecute()
        log.info("Starting execution of job ${getName()}. (${getJobEntries().size()} entries.)")
    }

    protected int executeInternal() {
		jobEntries.each {
			execute(it)
		}
		if (lastException) {
			throw lastException
		}
		return executionInfo.processedRows
	}

	protected void execute(JobEntry entry) {
		if (this.executionInfo.status==Status.ERROR) {
			return
		}
		try {
			entry.execute()
		}
		catch (Exception e) {
			lastException=e
			if (!continueOnError) {
				executionInfo.status=Status.ERROR
			}
		}
	}

	@Override
	public void postExecute() {
		super.postExecute();
		updateExecutionInfo()
		traceStatus()
	}

    void updateExecutionInfo() {
        executionInfo.processedRows=0
        jobEntries.each {
            executionInfo.processedRows+=it.executionInfo.processedRows
        }
    }

	public void simulate() {
		jobEntries.each { it.simulate() }
	}

	public void traceStatus() {
		if (log.infoEnabled) {
				log.info(getExecutionSummaryMessage())
		}
	}

	public String getExecutionSummaryMessage() {
		String name=toString().padRight(50).toString().substring(0,50)
		String dur=executionInfo.duration.toString().padLeft(10).toString().substring(0,10)
		String stat=executionInfo.status.toString().padLeft(10).toString().substring(0,10)
		String processedRows=executionInfo.processedRows.toString().padLeft(10).toString().substring(0,10)
		return """ Execution results for ${name}:
*********************************************************************************************
*  Name                                              *   Status   *  Time (ms)*        Rows *
*********************************************************************************************
* ${name} * ${stat} * ${dur} * ${processedRows} *
*********************************************************************************************
${jobEntries.join('\n')}
*********************************************************************************************
"""
	}
}
