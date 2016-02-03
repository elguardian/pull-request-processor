package org.jboss.pull.processor.processes;


public class ProcessorExecutionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessorExecutionException(String message, Throwable ex) {
		super(message, ex);
	}

}
