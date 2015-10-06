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
package org.disl.workflow

import org.disl.meta.Context
import org.disl.meta.MetaFactory


/**
 * Launcher for data integration processes implemented in DISL.
 * */
class DislLauncher {
	
	String[] arguments
	
	String className
	String contextName="default"
	
	/**
	 * Main methods expecting 2 arguments.
	 * First argument is className of Executable implementing DISL data integration process.
	 * Second argument is name of context which will be used for execution. This argument is optional, default context will be used by default.
	 * */
	public static void main(String[] args) {
		new DislLauncher(arguments: args).execute()
	}
	
	public void execute() {
		init()
		Context.setContextName(getContextName());
		def executable=MetaFactory.create(Class.forName(getClassName()))
		executable.execute()		
	}
	
	protected void init() {
		if (arguments.length<1 || arguments.length>2) {
			throw new IllegalArgumentException("Invalid argument count. Usage: [className] ([contextName])");
		}
		className=arguments[0]
		if (arguments.length==2) {
			contextName=arguments[1]
		}
	}
}
