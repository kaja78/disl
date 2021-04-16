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
package org.disl.pattern

import groovy.transform.CompileStatic

import org.disl.meta.Context
import org.disl.meta.Table


/**
 * Represents one operation to be performed as part of Pattern execution.
 * */
@CompileStatic
public abstract class Step extends AbstractExecutable {
	
	String name;

	/**
	 *  Decides if this should be executed. By default only steps of the same execution mode as the context execution mode are executed.
	 * */
	public boolean isToExecute() {
		Context.getContext().getExecutionMode().equals(this.getExecutionMode())
	}

	/**
	 * Step execution mode.
	 * */
	String getExecutionMode(){
		Context.EXECUTION_MODE_DEFAULT
	}

	Pattern pattern

	abstract String getCode();

	@Override
	public void simulate() {
		println "/*  Simulating step ${this}:*/"
		println """\t${code.replace("\n","\n\t")}""";
	}

	public String getName() {
		if (name==null) {
			return this.getClass().getSimpleName()
		}
		return name
	}
	
	public void setName(String name) {
		this.name=name
	}

	public Context getContext() {
		Context.getContext()
	}

	@Override
	public String toString() {
		return getName()
	}
}
