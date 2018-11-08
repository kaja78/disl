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

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import org.disl.meta.Context
import org.disl.pattern.Executable
import groovy.util.logging.Slf4j

/**
 * Handles parallel execution of job entries.
 * */
@Slf4j
@Singleton(lazy=true,strict=false)
class ParallelJobExecutor {
	int parallelJobsInProgress=0

	private ExecutorService executorService
	private ExecutorService parallelJobExecutorService
	
	private synchronized ExecutorService getParallelJobExecutorService() {
		if (parallelJobExecutorService==null) {
			parallelJobExecutorService=Executors.newCachedThreadPool()
		}
		return parallelJobExecutorService
	}
	
	private synchronized ExecutorService getExecutorService() {
		if (executorService==null) {
			executorService=Executors.newFixedThreadPool(Integer.parseInt(Context.getContextProperty("disl.parallelExecutorThreads", "4")))
		}
		return executorService
	}
	
	/**
	 * Execute Job's jobEntries in parallel.
	 * */
	void execute(Job job) {
		def parallelFutures=submitParallelJobTasks(job)
		def futures=submitTasks(job)
		try {
			checkResults(job,parallelFutures)
			checkResults(job,futures)
		} catch (Exception e) {
			throw new RuntimeException("Exception in asynchronous execution.",e)
		} finally {
			releaseParallelJob()
		}
	}
	
	synchronized void releaseParallelJob() {
		parallelJobsInProgress--
		if (parallelJobsInProgress==0) {
			shutdownExecutors()
		}
	}
	
	List<Future> submitParallelJobTasks(Job job) {
		Collection<Executable> executables=job.getJobEntries()
		Collection<Callable> parallelJobTasks=executables.findAll({isParallelJobEntry(it)}).collect({createCallable(job,it)})
		synchronized (this) {
			parallelJobsInProgress++
		}
		def service=getParallelJobExecutorService()
		return parallelJobTasks.collect({service.submit(it)})
	}
	
	protected List<Future> submitTasks(Job job) {
		Collection<Executable> executables=job.getJobEntries()
		Collection<Callable> tasks=executables.findAll({!isParallelJobEntry(it)}).collect({createCallable(job,it)})
		def service=getExecutorService()
		return tasks.collect({service.submit(it)})		
	}
	
	protected void shutdownExecutors() {
			if (executorService!=null) {
				executorService.shutdown()
				executorService=null
			}
			if (parallelJobExecutorService!=null) {
				parallelJobExecutorService.shutdown()
				parallelJobExecutorService=null
			}
	}
	
	protected boolean isParallelJobEntry(JobEntry jobEntry) {
		return (jobEntry.executable instanceof ParallelJob)
	}
	
	/**
	 * Check result of Executable execution. The result should be null. However Exception is thrown if execution failed.
	 * */
	protected void checkResults(Job job,List<Future> futures) {
		futures.each({
			try {
				it.get()
			} catch (Exception e) {
				if (!job.continueOnError) {
					log.info('Exception in parallel job execution. Shutting down parallel job executor service.')
				} else {
					log.info('Exception in parallel job execution. Continuing parallel job execution.')
				}
			}

		})
	}
	
	protected Callable createCallable(Job job,Executable executable) {
		Context parentContext=Context.getContext()
		return new Callable() {
			Object call() {
				Context.init(parentContext)
				job.execute(executable)
			}
		}
	}

}
