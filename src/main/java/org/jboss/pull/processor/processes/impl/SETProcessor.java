package org.jboss.pull.processor.processes.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jboss.pull.processor.AphroditeUtil;
import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.pull.processor.processes.ProcessingResult;
import org.jboss.pull.processor.processes.Processor;
import org.jboss.pull.processor.processes.ProcessorExecutionException;
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
	
	private List<Evaluator> rules;
	
	private ExecutorService service;
	
    public void init(Aphrodite aphrodite, StreamService streamService) {
    	this.aphrodite = aphrodite;
    	this.streamService = streamService;
    	this.rules = getRules();
    	this.service = Executors.newSingleThreadExecutor();
    }

    public List<ProcessingResult> process(URL url) throws ProcessorExecutionException {
    	try {
	    	Repository repository = aphrodite.getRepository(url);
	    	List<Patch> patches = aphrodite.getPatchesByStatus(repository, PatchStatus.OPEN);
	    	
	    	List<Future<ProcessingResult>> results = this.service.invokeAll(patches.stream().map(e -> new PatchProcessingTask(repository, e)).collect(Collectors.toList()));
	    	
	    	List<ProcessingResult> data = new ArrayList<>();
	    	for(Future<ProcessingResult> result : results) {
	    		try {
	    			data.add(result.get());
	    		} catch(Exception ex) {
	    			logger.log(Level.SEVERE, "ouch !" + ex);
	    		}
	    	}
	    	
	    	this.service.shutdown();
	    	
	    	return data;

		} catch(NotFoundException | InterruptedException ex) {
			throw new ProcessorExecutionException("processor execution failed", ex);
		}
    }
    
    private class PatchProcessingTask implements Callable<ProcessingResult> {

    	private Repository repository;
    	
    	private Patch patch;
    	
		public PatchProcessingTask(Repository repository, Patch patch) {
			this.repository = repository;
			this.patch = patch;
		}

		@Override
		public ProcessingResult call() throws Exception {

			logger.info("processing " + patch.getURL().toString());
    		List<Issue> issues = aphrodite.getIssuesAssociatedWith(patch);
    		// TODO: this should be in aphrodite place
    		List<Patch> relatedPatches = AphroditeUtil.getPatchesRelatedTo(aphrodite, patch);
    		Map<String, Object> data = new HashMap<>();
			EvaluatorContext context = new EvaluatorContext(aphrodite, streamService, repository, patch, issues, relatedPatches);
			
    		for(Evaluator rule : rules) {
    			logger.fine("repository " + repository.getURL() + "applying evaluator " + rule.name() + " to " + patch.getId());
    			rule.eval(context, data);
    		}
    		    		   		
    		return new ProcessingResult(data);
		}
    	
    }
    

    
    private List<Evaluator> getRules() {
    	ServiceLoader<Evaluator> rules = ServiceLoader.load(Evaluator.class);
    	List<Evaluator> tmp = new ArrayList<Evaluator>();
    	
    	for(Evaluator rule : rules) {
    		tmp.add(rule);
    	}
    	
    	return tmp;
    }
}
