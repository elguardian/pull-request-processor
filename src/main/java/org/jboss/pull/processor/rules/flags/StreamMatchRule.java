package org.jboss.pull.processor.rules.flags;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.pull.processor.AphroditeUtil;
import org.jboss.pull.processor.Messages;
import org.jboss.pull.processor.rules.LabelRuleResult;
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

public class StreamMatchRule implements Rule {

	@Override
	public String name() {
		return "bug doc rules";
	}

	@Override
	public RuleResult apply(RuleContext context) {
		try {
			StreamService service = context.getStreamService();
			Patch patch = context.getPatch();
			List<Issue> issues = context.getIssues();
			
			//  if there aren't any bug related then we show a message
			if(issues.isEmpty()) {
				return new MessageRuleResult(Messages.MISSING_BUG);
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
			Stream stream = AphroditeUtil.getStreamBy(service, patch);
			
			return (streams.contains(stream.getName())) ? new LabelRuleResult(stream.getName(), true) : 
				new MessageRuleResult(patch.getCodebase() + " not in any of issue stream " + toString(issues));
		} catch(NotFoundException nfe ) {
			return new MessageRuleResult(nfe.getMessage());
		}
	}
	
	private String toString(List<Issue> issues) {
		return issues.stream().map(e -> e.getTrackerId().get()).collect(Collectors.joining(", "));
	}



}
