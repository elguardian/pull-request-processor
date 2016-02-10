package org.jboss.pull.processor.evaluators.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.set.aphrodite.domain.Patch;

public class UpstreamEvaluator implements Evaluator {
	
	private Pattern UPSTREAM_NOT_REQUIRED = Pattern.compile(".*no.*upstream.*required.*", Pattern.CASE_INSENSITIVE);
	
	@Override
	public String name() {
		return "upstream required";
	}

	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {
		Patch patch = context.getPatch();
		List<Patch> related = context.getRelated();
		
		if(!UPSTREAM_NOT_REQUIRED.matcher(patch.getDescription()).find()) {
			if(!related.isEmpty()) {
				data.put("upstreamMessage", "missing upstream issue link");		
			} 
		}
	}
}
