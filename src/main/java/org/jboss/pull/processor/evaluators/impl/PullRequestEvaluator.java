package org.jboss.pull.processor.evaluators.impl;

import java.util.Map;

import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.pull.processor.processes.LinkResult;
import org.jboss.set.aphrodite.domain.Patch;

public class PullRequestEvaluator implements Evaluator {

	@Override
	public String name() {
		return "Stream Match Evaluator";
	}
	
	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {
		Patch patch = context.getPatch();
		data.put("pullRequest", new LinkResult(patch.getId(), patch.getURL()));

	}

}
