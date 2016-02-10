package org.jboss.pull.processor.evaluators.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.pull.processor.processes.IssueResult;
import org.jboss.set.aphrodite.domain.Issue;

public class IssuesRelatedEvaluator implements Evaluator {

	@Override
	public String name() {
		return "Issues Related evaluator";
	}

	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {
		List<Issue> issues = context.getIssues();
		Map<String, String> issueStream = new HashMap<>();
				
		for(Issue issue : issues) {
			String streams = Util.getStreams(issue);
			issueStream.put(issue.getTrackerId().get(), streams);
		}
		
		data.put("issuesRelated", issues.stream()
			.map(e -> new IssueResult(e.getTrackerId().get(), issueStream.get(e.getTrackerId().get()), e.getURL()) )
			.collect(Collectors.toList())
		);
	}

}
