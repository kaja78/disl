/*
 * Copyright 2015 - 2016 Karel H�bl <karel.huebl@gmail.com>.
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

import groovy.sql.Sql;

import org.disl.meta.Context
import org.disl.meta.MetaFactory
import org.disl.pattern.AbstractExecutable
import org.disl.pattern.ExecuteSQLQueryStep
import org.disl.pattern.Status;
import org.disl.test.DislTestCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TestJobs extends DislTestCase {
	
	@Before
    void init() {
		Context.setContextName("disl-test")
		Context.getContext().setExecutionMode('testing')
	}

	@Test
	public void testSerialJob() {
		Job job=MetaFactory.create(SerialTestingJob)
		job.execute()
		Assert.assertEquals(Status.FINISHED,job.executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[0].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[1].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[2].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[3].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[4].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[5].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[6].executionInfo.status)
		Assert.assertEquals(7,job.executionInfo.processedRows)
	}
	
	@Test
	public void testParallelJob() {
		Job job=MetaFactory.create(ParallelTestingJob)
		job.execute()
		Assert.assertEquals(Status.FINISHED,job.executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[0].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[1].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[2].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[3].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[4].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[5].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[6].executionInfo.status)
		Assert.assertEquals(7,job.executionInfo.processedRows)
	}

	@Test
	public void testSerialStopOnError() {
		TestingException expected
		Job job=MetaFactory.create(SerialFailingJob)
		try {
			job.execute()
		} catch (TestingException e) {
			expected=e
		}
		Assert.assertNotNull(expected)
		Assert.assertEquals(Status.ERROR,job.executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[0].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[1].executionInfo.status)
		Assert.assertEquals(Status.NEW,job.jobEntries[2].executionInfo.status)
		Assert.assertEquals(Status.NEW,job.jobEntries[3].executionInfo.status)
		Assert.assertEquals(Status.NEW,job.jobEntries[4].executionInfo.status)
		Assert.assertEquals(Status.NEW,job.jobEntries[5].executionInfo.status)
		Assert.assertEquals(Status.NEW,job.jobEntries[6].executionInfo.status)
		Assert.assertEquals(1,job.executionInfo.processedRows)
	}

	@Test
	public void testParallelStopOnError() {
		TestingException expected
		Job job=MetaFactory.create(ParallelFailingJob)
        try {
            job.execute()
        } catch (TestingException e) {
            expected=e
        }
        Assert.assertNotNull(expected)
        Assert.assertEquals(Status.ERROR,job.executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[0].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[1].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[2].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[3].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[4].executionInfo.status)
		Assert.assertEquals(Status.NEW,job.jobEntries[5].executionInfo.status)
		Assert.assertEquals(Status.NEW,job.jobEntries[6].executionInfo.status)
        Assert.assertEquals(1,job.executionInfo.processedRows)
	}

	@Test
	public void testSerialContinueOnError() {
		TestingException expected
		Job job=MetaFactory.create(SerialFailingJob)
		job.continueOnError=true
		try {
			job.execute()
		} catch (TestingException e) {
			expected=e
		}
		Assert.assertNotNull(expected)
		Assert.assertEquals(Status.ERROR,job.executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[0].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[1].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[2].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[3].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[4].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[5].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[6].executionInfo.status)
		Assert.assertEquals(2,job.executionInfo.processedRows)
	}

	@Test
    public void testParallelContinueOnError() {
		TestingException expected
		Job job=MetaFactory.create(ParallelFailingJob)
        job.continueOnError=true
        try {
            job.execute()
        } catch (TestingException e) {
            expected=e
        }
		Assert.assertNotNull(expected)
		Assert.assertEquals(Status.ERROR,job.executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[0].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[1].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[2].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[3].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[4].executionInfo.status)
		Assert.assertEquals(Status.ERROR,job.jobEntries[5].executionInfo.status)
		Assert.assertEquals(Status.FINISHED,job.jobEntries[6].executionInfo.status)
        Assert.assertEquals(2,job.executionInfo.processedRows)
    }

	@Test
	public void testMasterJob() {
		TestingException expected
		Job job=MetaFactory.create(MasterJob)
		try {
			job.execute()
		} catch (TestingException e) {
			expected=e
		}
		Assert.assertNotNull(expected)
		Assert.assertEquals(Status.ERROR,job.executionInfo.status)
	}




    @Test
	public void testExecuteTestingExecutable() {
		new TestingExecutable().execute()
	}

    @Test(expected = TestingException)
    public void testExecuteFailingTestingExecutable() {
        new TestingFailingExecutable().execute()
    }
	
	static class TestingJob extends ParallelJob {
		TestingJob() {
			addAll([new TestingExecutable()])
		}
	}

	static class SerialTestingJob extends Job {
		@Override
		void init() {
			super.init()
			addAll getTestingJobEntries()
		}
	}

	static class SerialFailingJob extends Job {
		@Override
		void init() {
			super.init()
			addAll getFailingJobEntries()
		}
	}


	static class ParallelTestingJob extends ParallelJob {
		@Override
		void init() {
			super.init()
			addAll getTestingJobEntries()
		}
	}

	static class ParallelFailingJob extends ParallelJob {
		@Override
		void init() {
			super.init()
			addAll getFailingJobEntries()
		}
	}

	static class MasterJob extends Job {
		@Override
		void init() {
			super.init()
			addType ParallelFailingJob
			addType ParallelTestingJob
		}
	}

	private static List<AbstractExecutable> getTestingJobEntries() {
		[
			new TestingJob(),
		 	new TestingJob(),
		 	new TestingJob(),
		 	new TestingJob(),
		 	new TestingJob(),
		 	new TestingExecutable(),
		 	new TestingExecutable()
		]
	}

	private static List<AbstractExecutable> getFailingJobEntries() {
		[
			new TestingExecutable(),
			new TestingFailingExecutable(),
			new TestingFailingExecutable(),
			new TestingFailingExecutable(),
			new TestingFailingExecutable(),
			new TestingFailingExecutable(),
			new TestingExecutable()
		]
	}


	static class TestingExecutable extends  ExecuteSQLQueryStep {
		
		@Override
		public Sql getSql() {
			return Context.getContext().getSql('default');
		}
		
		@Override
		public String getCode() {
			'select * from DUAL'
		}
		
		int executeInternal() {			
			assert Context.getContext().getExecutionMode().equals('testing')
			super.executeInternal()
			Thread.sleep(100)
			return 1
		}
		
		void simulate(){}
	}

	static class TestingFailingExecutable extends AbstractExecutable {

		@Override
		protected int executeInternal() {
			Thread.sleep(200)
			throw new TestingException()
		}

		@Override
		void simulate() {
			println('Execution will throw RuntimeException.')
		}
	}

    static class TestingException extends RuntimeException {
    }

}
