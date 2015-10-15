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
package org.disl.pattern;

import groovy.transform.CompileStatic;

import org.disl.meta.Context
import org.disl.meta.Initializable;
import org.disl.meta.MetaFactory;


/**
 * Represents set of operations (steps) to be performed to implement MDA pattern for given DISL data integration model object.
 * Pattern for table may generate DDL for table deployment into target database or execute replication or change data capture process.
 * Pattern for mapping may generate or execute data integration logic.
 * Patterns are use to define generic transformation of DISL model objects into artifacts.
 * */

public abstract class Pattern extends AbstractExecutable implements Initializable {
	
	private Collection<Step> steps=[]
	
	protected Pattern(){}
	
	public Collection<Step> getSteps() {
		String em=Context.getContext().getExecutionMode()
		steps.findAll {it.executionMode.equals(Context.getContext().getExecutionMode())}
	}
	
	public void add(Step step) {
		step.setPattern(this)
		steps.add(step)
	}
	
	public void add(Class<Step> type) {
		Step step=MetaFactory.create(type)		
		add(step)
	}
	
	public void add(Collection<Class<Step>> types) {
		types.each {add(it)}
	}
				
	@Override
	public int executeInternal() {
		long timestamp=System.currentTimeMillis();
		println "Executing pattern $this:"
		int processedRows=0		
		getSteps().each {it.execute();processedRows+=it.executionInfo.processedRows}		
		println "${this} executed in ${System.currentTimeMillis()-timestamp} ms"
		return processedRows		
	}
	
	@Override
	public void simulate() {
		println "Simulating pattern $this:"
		getSteps().each {it.simulate()}		
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	public abstract void init();

}
