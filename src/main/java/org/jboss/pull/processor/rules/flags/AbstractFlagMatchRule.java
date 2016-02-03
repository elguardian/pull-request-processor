package org.jboss.pull.processor.rules.flags;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.pull.processor.AphroditeUtil;
import org.jboss.pull.processor.rules.CheckedRuleResult;
import org.jboss.pull.processor.rules.MessageRuleResult;
import org.jboss.pull.processor.rules.Rule;
import org.jboss.pull.processor.rules.RuleContext;
import org.jboss.pull.processor.rules.RuleResult;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.spi.NotFoundException;
import org.jboss.set.aphrodite.spi.StreamService;

public abstract class AbstractFlagMatchRule implements Rule {

	@Override
	public RuleResult apply(RuleContext context) {
		try {
			StreamService service = context.getStreamService();
			Patch patch = context.getPatch();
			List<Issue> issues = context.getIssues();
			
			//  if there aren't any bug related then we show a message
			if(issues.isEmpty()) {
				return new MessageRuleResult("No issues found in patch, " + name() + " not applied");
			}
			
			// TODO get the stream based on repository and codebase
			Stream stream = AphroditeUtil.getStreamBy(service, patch);
			
			List<Issue> streamRelated = new ArrayList<>();
			Map<String, String> issueStream = new HashMap<>();
			EnumSet<FlagStatus> set = EnumSet.of(FlagStatus.ACCEPTED, FlagStatus.SET);
			for(Issue issue : issues) {
				Map<String, FlagStatus> statuses = issue.getStreamStatus();
				for(Map.Entry<String, FlagStatus> status : statuses.entrySet()) {
					if(set.contains(status.getValue())) {
						if(!streamRelated.contains(issue)) {
							streamRelated.add(issue);
							issueStream.put(issue.getTrackerId().get(), status.getKey());
						}
					}
				}
			}
			
			if(streamRelated.isEmpty()) {
				return new MessageRuleResult("No issues related to the patch stream, " + name() + " not applied");
			} else if(streamRelated.size() == 1) {
				Issue issueRelated = streamRelated.get(0);
				if(issueStream.containsKey(issueRelated.getTrackerId().get())) {
					return calculate(issueRelated);
				} else {
					return new MessageRuleResult("Issue found but not does belong to stream " + stream + " " + issueRelated.getSummary());
				}
			} else {
				return new MessageRuleResult("More than one issue related to the patch stream, "  + name() + " not applied " +toString(streamRelated));
			}
		 
		} catch(NotFoundException nfe) {
			return new MessageRuleResult(nfe.getMessage());
		}
	}

	public abstract RuleResult calculate(Issue issue);
	
	private String toString(List<Issue> issues) {
		return issues.stream().map(e -> e.getTrackerId().get()).collect(Collectors.joining(", "));
	}

}
