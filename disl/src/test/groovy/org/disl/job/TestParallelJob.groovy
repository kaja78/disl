package org.disl.job

import org.disl.pattern.Executable
import org.junit.Test

class TestParallelJob {
	
	@Test
	public void testExecute() {
		new ParallelJob().addAll([new TestingJob(),
				new TestingJob(),
				new TestingJob(),
				new TestingJob(),
				new TestingJob(),
				new TestingExecutable(),
				new TestingExecutable()
			]).execute()
	}
	
	static class TestingJob extends ParallelJob {
		TestingJob() {
			addAll([new TestingExecutable()])
		}
	}

	static class TestingExecutable implements Executable {
		void execute() {
			Thread.sleep(500)
		}
		void simulate(){}
	}

}
