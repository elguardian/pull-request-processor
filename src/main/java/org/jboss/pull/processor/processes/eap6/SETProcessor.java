package org.jboss.pull.processor.processes.eap6;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.pull.processor.AphroditeUtil;
import org.jboss.pull.processor.processes.Processor;
import org.jboss.pull.processor.processes.ProcessorExecutionException;
import org.jboss.pull.processor.rules.Rule;
import org.jboss.pull.processor.rules.RuleContext;
import org.jboss.pull.processor.rules.RuleResult;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.domain.PatchStatus;
import org.jboss.set.aphrodite.domain.Repository;
import org.jboss.set.aphrodite.spi.NotFoundException;
import org.jboss.set.aphrodite.spi.StreamService;

public class SETProcessor implements Processor {
	
	private static Logger logger = Logger.getLogger("org.jboss.pull.processor.processes");
	
	private Aphrodite aphrodite;
	
	private StreamService streamService;
	
	private List<Rule> rules;
	
	private ExecutorService service;
	
    public void init(Aphrodite aphrodite, StreamService streamService) {
    	this.aphrodite = aphrodite;
    	this.streamService = streamService;
    	this.rules = getRules();
    	this.service = Executors.newSingleThreadExecutor();
    }

    public void process(URL url) throws ProcessorExecutionException {
    	try {
	    	Repository repository = aphrodite.getRepository(url);
	    	List<Patch> patches = aphrodite.getPatchesByStatus(repository, PatchStatus.OPEN);
	    	
	    	for(Patch patch : patches) {
	    		this.service.submit(new PatchProcessingTask(repository, patch));
	    	}
	    	
	    	this.service.shutdown();
		} catch(NotFoundException ex) {
			throw new ProcessorExecutionException("processor execution failed", ex);
		}
    }
    
    private class PatchProcessingTask implements Runnable {

    	private Repository repository;
    	
    	private Patch patch;
    	
		public PatchProcessingTask(Repository repository, Patch patch) {
			this.repository = repository;
			this.patch = patch;
		}

		@Override
		public void run() {
			try {
				logger.info("processing " + patch.getURL().toString());
	    		
		    	List<RuleResult> results = new ArrayList<RuleResult>();
	    		List<Issue> issues = aphrodite.getIssuesAssociatedWith(patch);
	    		// TODO: this should be in aphrodite place
	    		List<Patch> relatedPatches = AphroditeUtil.getPatchesRelatedTo(aphrodite, patch);
	    		for(Rule rule : rules) {
	    			logger.fine("repository " + repository.getURL() + "applying rule " + rule.name() + " to " + patch.getId());
	    			RuleContext context = new RuleContext(aphrodite, streamService, patch, issues, relatedPatches);
	    			results.add(rule.apply(context));
	    		}
	    		
	    		System.out.println("messages " + patch.getURL().toString());
	    		for(RuleResult result : results) {
	    			System.out.println(result.toString());
	    		}
			} catch(MalformedURLException | URISyntaxException ex) {
				logger.log(Level.SEVERE, patch.getURL() + " failed ", ex);
			}
		}
    	
    }
    
    
    private List<Rule> getRules() {
    	ServiceLoader<Rule> rules = ServiceLoader.load(Rule.class);
    	List<Rule> tmp = new ArrayList<Rule>();
    	
    	for(Rule rule : rules) {
    		tmp.add(rule);
    	}
    	
    	return tmp;
    }
}
