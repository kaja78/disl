/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
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
package org.disl.pattern;

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j;

import org.disl.meta.Context
import org.disl.meta.Initializable;
import org.disl.meta.MetaFactory

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection;


/**
 * Represents set of operations (steps) to be performed to implement MDA pattern for given DISL data integration model object.
 * Pattern for table may generate DDL for table deployment into target database or execute replication or change data capture process.
 * Pattern for mapping may generate or execute data integration logic.
 * Patterns are use to define generic transformation of DISL model objects into artifacts.
 * */
@Slf4j
@CompileStatic
public abstract class Pattern extends AbstractExecutable implements Initializable {
	
	List<Step> steps=[]
	
	protected Pattern(){}
	

	
	public void add(Step step) {
		if (step.isToExecute()) {
			step.setPattern(this)
			steps.add(step)
		}
	}
	
	public void add(Class<Step> type) {
		Step step=(Step)MetaFactory.create(type)
		add(step)
	}
	
	public void add(Collection<Class<Step>> types) {
		types.each {add(it)}
	}
				
	@Override
	public int executeInternal() {
		long timestamp=System.currentTimeMillis();
		log.debug("Executing pattern $this:")
		int processedRows=0		
		steps.each {it.execute();processedRows+=it.executionInfo.processedRows}
		log.debug("${this} processed ${processedRows} rows in ${System.currentTimeMillis()-timestamp} ms.")
		return processedRows		
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

	/**
	 * Copy code from all pattern steps to clipboard.
	 * */
	public void copyCodeToClipboard() {
		StringBuffer sb=new StringBuffer()
		steps.each {sb.append(it.getCode())}
		StringSelection ss = new StringSelection(sb.toString());
		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss,null);
		} catch (Exception e) {
		}
	}
	
	public abstract void init();

	void traceStepExecutionInfo() {
		print stepExecutionInfoMessage
	}

	public String getStepExecutionInfoMessage() {
		""" Step execution info:
*********************************************************************************************
*  Name                                              *   Status   *  Time (ms)*        Rows *
*********************************************************************************************
${steps.collect({ formatExecutionInfo(it.name, it.executionInfo) }).join('\n')}
*********************************************************************************************
"""
	}

	String formatExecutionInfo(String name,ExecutionInfo executionInfo) {
		name=name.padRight(50).toString().substring(0,50)
		String dur=executionInfo.duration.toString().padLeft(10).toString().substring(0,10)
		String stat=executionInfo.status.toString().padLeft(10).toString().substring(0,10)
		String processedRows=Integer.toString(executionInfo.processedRows).padLeft(10).toString().substring(0,10)
		return "* ${name} * ${stat} * ${dur} * ${processedRows} *"
	}

}
