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

import org.disl.meta.Context


/**
 * Represents one operation to be performed as part of Pattern execution.
 * */
public abstract class Step extends AbstractExecutable {
	

	String name;

	/**
	 * Step execution mode. For pattern only steps of the same execution mode as the context execution mode are created.  
	 * */
	String executionMode=Context.EXECUTION_MODE_DEFAULT

	def pattern='';

	String getCode() {
		if (getPattern() instanceof Closure) {
			getPattern().call();
		}
		getPattern();
	}

	@Override
	public void simulate() {
		println "  Simulating step ${this}:"
		println """\t${code.replace("\n","\n\t")}""";
	}

	public String getName() {
		if (name==null) {
			return this.getClass().getSimpleName()
		}
		return name
	}

	@Override
	public String toString() {
		return getName()
	}
}
