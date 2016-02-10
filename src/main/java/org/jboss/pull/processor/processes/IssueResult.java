package org.jboss.pull.processor.processes;

import java.net.URL;

public class IssueResult {
	private URL link;
	
	private String label;
	
	private String stream;
	
	public IssueResult(String label, String stream, URL link) {
		this.link = link;
		this.label = label;
		this.stream = stream;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getStream() {
		return stream;
	}
	
	public URL getLink() {
		return link;
	}
}