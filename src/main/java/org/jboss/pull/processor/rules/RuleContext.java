package org.jboss.pull.processor.rules;

import java.util.List;

import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.spi.StreamService;



public class RuleContext {

	private Aphrodite aphrodite;
	
	private StreamService streamService;
	
	private Patch patch;
	
	private List<Issue> issues;
	
	private List<Patch> related;
	
	public RuleContext(Aphrodite aphrodite, StreamService streamService, Patch patch, List<Issue> issues, List<Patch> related) {
		this.aphrodite = aphrodite;
		this.streamService = streamService;
		this.patch = patch;
		this.issues = issues;
		this.related = related;
	}
	
	public Aphrodite getAphrodite() {
		return aphrodite;
	}
	
	public StreamService getStreamService() {
		return streamService;
	}
	
	public Patch getPatch() {
		return patch;
	}
	
	public List<Issue> getIssues() {
		return issues;
	}
	
	public List<Patch> getRelated() {
		return related;
	}
	
}
