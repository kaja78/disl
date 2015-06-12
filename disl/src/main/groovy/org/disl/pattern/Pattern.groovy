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



public abstract class Pattern implements Executable {
	abstract Collection<Step> getSteps()
	
	@Override
	public void execute() {
		long timestamp=System.currentTimeMillis();
		println "Executing pattern $this:"
		steps.each {it.execute()}
		println "${this} executed in ${System.currentTimeMillis()-timestamp} ms"		
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

}
