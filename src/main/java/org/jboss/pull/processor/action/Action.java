package org.jboss.pull.processor.action;

import java.util.List;

import org.jboss.pull.processor.processes.ProcessingResult;

public interface Action {

	void execute(List<ProcessingResult> data);
	
}
