package org.jboss.pull.processor.rules;

public class CheckedRuleResult extends RuleResult {

	private String message;
	
	public CheckedRuleResult(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	
	@Override
	public String toString() {
		return "Checked [" + message + "]";
	}
}
