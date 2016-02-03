package org.jboss.pull.processor.rules.flags;

import org.jboss.pull.processor.rules.LabelRuleResult;
import org.jboss.pull.processor.rules.RuleResult;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;

public class QEFlagMatchRule extends AbstractFlagMatchRule {

	@Override
	public String name() {
		return "QE Flag Match rule";
	}

	@Override
	public RuleResult calculate(Issue issue) {
		FlagStatus status = issue.getStage().getStatus(Flag.QE);
		return new LabelRuleResult("Needs qa_ack", !status.equals(FlagStatus.ACCEPTED)); 
	}

}
