package org.jboss.pull.processor.rules.flags;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jboss.pull.processor.Messages;
import org.jboss.pull.processor.rules.CheckedRuleResult;
import org.jboss.pull.processor.rules.MessageRuleResult;
import org.jboss.pull.processor.rules.RuleContext;
import org.jboss.pull.processor.rules.Rule;
import org.jboss.pull.processor.rules.RuleResult;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Patch;

public class UpstreamRules implements Rule {
	
	private Pattern UPSTREAM_NOT_REQUIRED = Pattern.compile(".*no.*upstream.*required.*", Pattern.CASE_INSENSITIVE);
	
	@Override
	public String name() {
		return "upstream required";
	}

	@Override
	public RuleResult apply(RuleContext context) {
		Patch patch = context.getPatch();
		List<Patch> related = context.getRelated();
		
		if(!UPSTREAM_NOT_REQUIRED.matcher(patch.getDescription()).find()) {
			if(related.isEmpty()) {
				return new MessageRuleResult("Missing upstream or not upstream PR related");
			} else {
				// we are supossing that a PR related is upstream.... not true but just an approach
				return new CheckedRuleResult("upstreams found " + toString(related));
			}
		} 
		return new CheckedRuleResult("No upstream required");
	}
	
	private String toString(List<Patch> patches) {
		return patches.stream().map(e -> e.getId()).collect(Collectors.joining(", "));
	}
}
