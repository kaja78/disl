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
package org.disl.workflow;

import org.disl.pattern.Executable;
import org.disl.pattern.ExecutionInfo;

class JobEntry implements Executable {
	
	Executable executable
			
	ExecutionInfo getExecutionInfo() {
		executable.executionInfo
	}
	
	void execute() {
		executable.execute()
	}
	
	void simulate() {
		executable.simulate()
	}
	
	String toString() {
		String name=executable.toString().padRight(50).toString().substring(0,50)
		String dur=executionInfo.duration.toString().padLeft(10).toString().substring(0,10)
		String stat=executionInfo.status.toString().padLeft(10).toString().substring(0,10)
		String processedRows=executionInfo.processedRows.toString().padLeft(10).toString().substring(0,10)
		return "* ${name} * ${stat} * ${dur} * ${processedRows} *"			
	}
	
}