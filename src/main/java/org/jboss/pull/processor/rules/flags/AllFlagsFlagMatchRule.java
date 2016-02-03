package org.jboss.pull.processor.rules.flags;

import org.jboss.pull.processor.rules.LabelRuleResult;
import org.jboss.pull.processor.rules.RuleResult;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;

public class AllFlagsFlagMatchRule extends AbstractFlagMatchRule {

	@Override
	public String name() {
		return "Has all Flags Match rule";
	}

	@Override
	public RuleResult calculate(Issue issue) {
		boolean hasAllFlags = true;
		
		for(Flag flag : Flag.values()) {
			FlagStatus status = issue.getStage().getStatus(flag);
			if(!status.equals(FlagStatus.ACCEPTED)) {
				hasAllFlags = false;
				break;
			}
		}
		
		
		return new LabelRuleResult("Has All Acks", hasAllFlags); 
	}
}
