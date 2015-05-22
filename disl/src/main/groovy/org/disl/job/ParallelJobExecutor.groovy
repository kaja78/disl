package org.disl.job

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.InstanceOfQueryExp;

import org.disl.job.Job.JobEntry
import org.disl.pattern.Executable

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
			executorService=Executors.newFixedThreadPool(4)
		}
		return executorService
	}
	
	
	void execute(Job job) {
		def parallelFutures=submitParallelJobTasks(job.getJobEntries())
		def futures=submitTasks(job.getJobEntries())	
		try {
			checkResults(parallelFutures)
			checkResults(futures)
		} catch (Exception e) {
			throw new RuntimeException("Exception in asynchronous execution.")
		} finally {
			releaseParallelJob()
			job.traceStatus()
		}
	}
	
	synchronized void releaseParallelJob() {
		parallelJobsInProgress--
		if (parallelJobsInProgress==0) {
			shutdownExecutors()
		}
	}
	
	List<Future> submitParallelJobTasks(Collection<Executable> executables) {
		Collection<Callable> parallelJobTasks=executables.findAll({isParallelJobEntry(it)}).collect({createCallable(it)})
		synchronized (this) {
			parallelJobsInProgress++
		}
		def service=getParallelJobExecutorService()
		return parallelJobTasks.collect({service.submit(it)})
	}
	
	List<Future> submitTasks(Collection<Executable> executables) {
		Collection<Callable> tasks=executables.findAll({!isParallelJobEntry(it)}).collect({createCallable(it)})
		def service=getExecutorService()
		return tasks.collect({service.submit(it)})		
	}
	
	void shutdownExecutors() {
			if (executorService!=null) {
				executorService.shutdown()
				executorService=null
			}
			if (parallelJobExecutorService!=null) {
				parallelJobExecutorService.shutdown()
				parallelJobExecutorService=null
			}
	}
	
	boolean isParallelJobEntry(JobEntry jobEntry) {
		return (jobEntry.executable instanceof ParallelJob)
	}
	
	/**
	 * Check result of Executable execution. The result should be null. However Exception is thrown if execution
	 * */
	void checkResults(List<Future> futures) {
		futures.each({it.get()})
	}
	
	Callable createCallable(Executable executable) {
		return new Callable() {
			Object call() {
				try {
					executable.execute();
				} catch (Exception e) {
					e.printStackTrace();
					throw e;
				}
			}
		}
	}

}
