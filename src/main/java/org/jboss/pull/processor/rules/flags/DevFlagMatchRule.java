package org.jboss.pull.processor.rules.flags;

import org.jboss.pull.processor.rules.LabelRuleResult;
import org.jboss.pull.processor.rules.RuleResult;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;

public class DevFlagMatchRule extends AbstractFlagMatchRule {

	@Override
	public String name() {
		return "Dev Flag Match rule";
	}

	@Override
	public RuleResult calculate(Issue issue) {
		FlagStatus status = issue.getStage().getStatus(Flag.DEV);
		return new LabelRuleResult("Needs devel_ack", !status.equals(FlagStatus.ACCEPTED)); 
	}

}
