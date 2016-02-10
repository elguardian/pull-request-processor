package org.jboss.pull.processor.evaluators.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.pull.processor.AphroditeUtil;
import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.spi.StreamService;

public class StreamsEvaluator implements Evaluator {

	@Override
	public String name() {
		return "Stream Match Evaluator";
	}
	
	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {

		StreamService service = context.getStreamService();
		Patch patch = context.getPatch();
		List<Issue> issues = context.getIssues();
		
		//  if there aren't any bug related then we show a message
		if(issues.isEmpty()) {
			data.put("streams", Collections.<String>emptyList());
		}
		
		List<String> streams = new ArrayList<>();
		EnumSet<FlagStatus> set = EnumSet.of(FlagStatus.ACCEPTED, FlagStatus.SET);
		for(Issue issue : issues) {
			Map<String, FlagStatus> statuses = issue.getStreamStatus();
			for(Map.Entry<String, FlagStatus> status : statuses.entrySet()) {
				if(set.contains(status.getValue())) {
					String streamName = status.getKey();
					streams.add(streamName.substring(streamName.length() - 5));
				}
			}
		}
		
		// TODO get the stream based on repository and codebase
		List<Stream> stream = AphroditeUtil.getStreamBy(service, patch);
		List<String> streamsStr = stream.stream().map(e -> e.getName()).collect(Collectors.toList());
		data.put("streams", streamsStr);

	}

}
