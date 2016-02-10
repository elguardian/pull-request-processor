package org.jboss.pull.processor.evaluators.impl;

import java.util.Map;

import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.set.aphrodite.domain.Patch;

public class BranchEvaluator implements Evaluator {

	@Override
	public String name() {
		return "labels evaluator";
	}

	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {
		Patch patch = context.getPatch();
		data.put("branch", patch.getCodebase().getName());
	}

}
