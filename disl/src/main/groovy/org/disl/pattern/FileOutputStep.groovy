package org.disl.pattern

class FileOutputStep extends Step {
	File file;
	
	@Override
	public void execute() {
		file.getParentFile().mkdirs()
		file.createNewFile()
		file.write(getCode())
	}

}
