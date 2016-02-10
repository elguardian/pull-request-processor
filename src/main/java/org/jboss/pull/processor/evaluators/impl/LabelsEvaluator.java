package org.jboss.pull.processor.evaluators.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.pull.processor.processes.LabelResult;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Patch;

public class LabelsEvaluator implements Evaluator {

	private static Logger logger = Logger.getLogger("org.jboss.pull");
	
	@Override
	public String name() {
		return "Labels evaluator";
	}

	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {

		Patch patch = context.getPatch();
		List<Issue> issues = context.getIssues();
		
		//  if there aren't any bug related then we show a message
		if(issues.isEmpty()) {
			logger.log(Level.WARNING, "No issues found in patch, " + name() + " not applied to " + patch.getURL());
		}
		

		Map<String, List<LabelResult>> labels = new HashMap<>();
		data.put("labels", labels);
		for(Issue issue : issues) {
			List<LabelResult> tmp = new ArrayList<>();
			labels.put(issue.getTrackerId().get(), tmp);
			
			boolean hasAllFlags = true;
			for(Flag flag : Flag.values()) {
				FlagStatus status = issue.getStage().getStatus(flag);
				if(!status.equals(FlagStatus.ACCEPTED)) {
					hasAllFlags = false;
					break;
				}
			}
			
			if(hasAllFlags) {
				tmp.add(new LabelResult("Has all ack", true));
			} else {
				for(Flag flag : Flag.values()) {
					FlagStatus status = issue.getStage().getStatus(flag);
					tmp.add(new LabelResult(flag.name().toLowerCase() + "_ack", status.equals(FlagStatus.ACCEPTED)));
				}
			}
			
			
		}		
	}
}
