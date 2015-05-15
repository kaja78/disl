package org.disl.job

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

import javax.management.InstanceOfQueryExp;

import org.disl.job.Job.JobEntry
import org.disl.pattern.Executable

@Singleton
class JobThreadPool {

	private ExecutorService executorService=Executors.newFixedThreadPool(4)
	private ExecutorService parallelJobExecutorService=Executors.newCachedThreadPool()

	void execute(Job job) {
		Collection<Executable> executables=job.getJobEntries()
		Collection<Callable> parallelJobTasks=executables.findAll({isParallelJobEntry(it)}).collect({createCallable(it)})
		Collection<Callable> tasks=executables.findAll({!isParallelJobEntry(it)}).collect({createCallable(it)})
		def parallelFutures=parallelJobExecutorService.invokeAll(parallelJobTasks)
		def futures=executorService.invokeAll(tasks)
		try {
			checkResults(parallelFutures)
			checkResults(futures)
		} catch (Exception e) {
			throw new RuntimeException("Exception in asynchronous execution.")
		} finally {
			job.traceStatus()
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
