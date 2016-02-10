package org.jboss.pull.processor.processes;

import java.util.HashMap;
import java.util.Map;


public class ProcessingResult {
		
	private Map<String, Object> data;
	
	public ProcessingResult(Map<String, Object> data) {
		this.data = data;
	}
	
	public ProcessingResult() {
		this.data = new HashMap<>();
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
}