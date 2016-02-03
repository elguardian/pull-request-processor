package org.jboss.pull.processor;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.jboss.pull.processor.processes.Processor;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.JsonStreamService;
import org.jboss.set.aphrodite.spi.NotFoundException;
import org.jboss.set.aphrodite.spi.StreamService;

public class Main {

	private Aphrodite aphrodite;
	
	public void start(String streamName) throws Exception {
    	aphrodite = Aphrodite.instance();
    	
		StreamService streamService = getStreamService();

    	ServiceLoader<Processor> processors = ServiceLoader.load(Processor.class);
    	
    	List<URL> urls = null;
    	if(streamName == null) {
    		urls = AphroditeUtil.findAllRepositories(streamService);
    	} else {
    		urls = AphroditeUtil.findAllRepositoriesInStream(streamService, streamName);
    	}

    	for(URL url : urls) {
	    	for(Processor processor : processors) {
	    		processor.init(aphrodite, streamService);
	    		processor.process(url);
	    	}
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


    private static String usage() {
        StringBuilder usage = new StringBuilder();
        usage.append("Enable processing via any combination of:\n");
        usage.append("-Dmerge\n");
        usage.append("-Dmilestone\n");
        return usage.toString();
    }

    private static String usageMerge() {
        StringBuilder usage = new StringBuilder();
        usage.append("java -jar pull-processor-1.0-SNAPSHOT.jar <property name of the target branch on github> <property name of dedicated jenkins merge job>\n\n");
        usage.append(common());
        return usage.toString();
    }

    private static StringBuilder common() {
        StringBuilder usage = new StringBuilder();
        usage.append("optional system properties:\n");
        usage.append("-Dprocessor.properties.file defaults to \"./processor.properties\"\n");
        usage.append("-Ddryrun=true to run without changing anything, i.e. simulated run, defaults to false\n");
        return usage;
    }
}
