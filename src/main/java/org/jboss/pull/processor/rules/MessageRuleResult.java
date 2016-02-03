package org.jboss.pull.processor.rules;

public class MessageRuleResult extends RuleResult {

	private String message;
	
	public MessageRuleResult(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Message [" + message + "]";
	}
}
