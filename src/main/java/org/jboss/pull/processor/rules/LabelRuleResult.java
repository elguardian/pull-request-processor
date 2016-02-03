package org.jboss.pull.processor.rules;


public class LabelRuleResult extends RuleResult {

	private String flag;
	
	private boolean add;
	
	public LabelRuleResult(String flag, boolean add) {
		this.flag = flag;
		this.add = add;
	}
	
	
	public String getLabel() {
		return flag;
	}
	
    public boolean isAdded() {
    	return add;
    }
	
	@Override
	public String toString() {
		return "Label " + (add ? "added" : "removed") + "[" + flag + "]";
	}
}
