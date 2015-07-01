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

class ExecutionInfo {

	/**
	 * Get runtime status.
	 * *
	 * @return
	 */
	Status status

	/**
	 * Get execution start time.
	 * */
	Long startTime

	/**
	 * Get execution end time.
	 */
	Long endTime

	/**
	 * Get number of processed rows.
	 * */
	int processedRows=0
	
	Exception exception

	/**
	 * Get execution duration in miliseconds.
	 * *
	 * @return
	 */
	Long getDuration() {
		if (startTime==null) {
			return null;
		}
		if (endTime==null) {
			return System.currentTimeMillis()-startTime
		}
		return endTime-startTime
	}
	
	void start() {
		startTime=System.currentTimeMillis()
		status=Status.RUNNING
	}
	
	void finish() {
		endTime=System.currentTimeMillis()
		status=Status.FINISHED
	}
	
	void error(Exception e) {
		endTime=System.currentTimeMillis()
		status=Status.ERROR
		exception=e
	}
}


