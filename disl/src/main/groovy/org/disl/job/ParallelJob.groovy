package org.disl.job

import org.disl.pattern.Executable

class ParallelJob extends Job {

	int threadCount=4

	@Override
	public void execute() {
		ParallelJobExecutor.instance.execute(this)
	}

}
