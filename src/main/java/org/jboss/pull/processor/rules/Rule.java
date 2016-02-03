package org.jboss.pull.processor.rules;


public interface Rule {

	public abstract String name();
	
    public abstract RuleResult apply(RuleContext context);
    
}
