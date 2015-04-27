package org.disl.job

import org.disl.meta.Context;
import org.disl.meta.MetaFactory;

class DislLauncher {
	
	String[] arguments
	
	String className
	String contextName="default"
	
	
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
