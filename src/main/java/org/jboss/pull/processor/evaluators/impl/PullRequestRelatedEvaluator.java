package org.jboss.pull.processor.evaluators.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.pull.processor.AphroditeUtil;
import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.pull.processor.processes.LinkResult;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.spi.StreamService;

public class PullRequestRelatedEvaluator implements Evaluator {

	@Override
	public String name() {
		return "Pull Request Related Evaluator";
	}

	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {
		List<Patch> relatedPatches = context.getRelated();
		StreamService service = context.getStreamService();
		
		// TODO get the stream based on repository and codebase

		List<LinkResult> links = new ArrayList<>();
		for(Patch patch : relatedPatches) {
			List<Stream> stream = AphroditeUtil.getStreamBy(service, patch);
			
			String label = "(" + stream.stream().map(e -> e.getName()).collect(Collectors.joining(", ")) + ")";
			
			links.add(new LinkResult(patch.getId() + " " + label, patch.getURL()));
		}
		
		
		data.put("pullRequestsRelated", links);

	}

}
