package org.jboss.pull.processor;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.jboss.pull.processor.action.Action;
import org.jboss.pull.processor.processes.ProcessingResult;
import org.jboss.pull.processor.processes.Processor;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.JsonStreamService;
import org.jboss.set.aphrodite.spi.NotFoundException;
import org.jboss.set.aphrodite.spi.StreamService;

public class Main {

	public static Logger logger = Logger.getLogger("org.jboss.pull.processor");
	
	private Aphrodite aphrodite;
	
	public void start(String streamName) throws Exception {

    	aphrodite = Aphrodite.instance();
    	
		StreamService streamService = getStreamService();

    	List<URL> urls = null;
    	if(streamName == null) {
    		urls = AphroditeUtil.findAllRepositories(streamService);
    	} else {
    		urls = AphroditeUtil.findAllRepositoriesInStream(streamService, streamName);
    	}
    	
    	ServiceLoader<Processor> processors = ServiceLoader.load(Processor.class);
    	List<ProcessingResult> data = new ArrayList<>();
    	for(Processor processor : processors) {
    		for(URL url : urls) {
	    		processor.init(aphrodite, streamService);
	    		data.addAll(processor.process(url));
	    	}
    	}

    	ServiceLoader<Action> actions = ServiceLoader.load(Action.class);
    	for(Action action : actions) {
    		action.execute(data);
    	}
	}
	

	
	private StreamService getStreamService() throws NotFoundException {
		JsonStreamService service = new JsonStreamService(aphrodite);
		service.loadStreamData();
		return service;
	}
	
    public static void main(String[] argv) throws Exception {
    	new Main().start(argv[0]);
    }

}
