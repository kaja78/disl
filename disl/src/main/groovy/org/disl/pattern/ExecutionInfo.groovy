package org.disl.pattern;

public interface ExecutionInfo {

	/**
	 * Get execution duration in miliseconds.
	 * *
	 * @return
	 */
	public abstract Long getDuration()

	/**
	 * Get execution end time.
	 */
	public abstract Long getEndTime()

	/**
	 * Get number of processed rows.
	 * */
	public abstract Long getProcessedRows()

	/**
	 * Get execution start time.
	 * */
	public abstract Long getStartTime()

	/**
	 * Get runtime status.
	 * *
	 * @return
	 */
	public abstract Status getStatus()
	
	abstract class Adapter implements ExecutionInfo {
		
		Status status
		Long startTime
		Long endTime
		Long processedRows
		
		Long getDuration() {
			if (startTime==null) {
				return null;
			}
			if (endTime==null) {
				return System.currentTimeMillis()-startTime
			}
			return endTime-startTime
		}
	}

}
