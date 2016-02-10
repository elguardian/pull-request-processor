package org.jboss.pull.processor.processes;

public class LabelResult {
	private boolean isOk;
	
	private String name;
	
	public LabelResult(String name, boolean isOk) {
		this.name = name;
		this.isOk = isOk;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isOk() {
		return isOk;
	}
	
	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}
}