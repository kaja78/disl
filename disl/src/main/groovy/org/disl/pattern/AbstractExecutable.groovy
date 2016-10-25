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
package org.disl.pattern;

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.disl.meta.MetaFactory

@Slf4j
@CompileStatic
abstract class AbstractExecutable implements Executable {
	
	boolean ignoreErrors=false
	ExecutionInfo executionInfo=new ExecutionInfo()
	
	public ExecutionInfo getExecutionInfo() {
		executionInfo
	}
	
	/**
	 * Execute the step and return number of processed rows.
	 * */
	protected abstract int executeInternal();
	
	@Override
	public final void execute() {
		try {
			executionInfo.start()
			beforeExecute()
			executionInfo.processedRows=executeInternal()
			executionInfo.finish()
		} catch (Exception e) {
			executionInfo.error(e)
			handleException(e)		
		} finally {
			postExecute()
		}
	}
	
	public void handleException(Exception e) {
		if (!isIgnoreErrors()) {
			log.error("${e.class.name} executing $this: ${e.message}",e)
			throw e
		}
	}
	
	/**
	 * Hook to implement before execution logic.
	 * */
	void beforeExecute() {	
	}
	
	/**
	 * Hook to implement post execution logic.
	 * */
	void postExecute() {		
	}
	
	Collection createAll(String packageName,Class assignableFrom) {
		return MetaFactory.createAll(packageName, assignableFrom)		
	}

}
