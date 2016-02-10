package org.jboss.pull.processor.processes;

import java.net.URL;

public class LinkResult {
	private URL link;
	
	private String label;
	
	public LinkResult(String label, URL link) {
		this.link = link;
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public URL getLink() {
		return link;
	}
}