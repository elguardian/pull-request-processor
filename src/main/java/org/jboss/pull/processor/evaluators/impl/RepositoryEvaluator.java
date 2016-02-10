package org.jboss.pull.processor.evaluators.impl;

import java.util.Map;

import org.jboss.pull.processor.AphroditeUtil;
import org.jboss.pull.processor.evaluators.Evaluator;
import org.jboss.pull.processor.evaluators.EvaluatorContext;
import org.jboss.pull.processor.processes.LinkResult;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.domain.Repository;
import org.jboss.set.aphrodite.spi.StreamService;

public class RepositoryEvaluator implements Evaluator {

	@Override
	public String name() {
		return "Component evaluator";
	}

	@Override
	public void eval(EvaluatorContext context, Map<String, Object> data) {
		Repository repository = context.getRepository();
		StreamService streamService = context.getStreamService();
		Patch patch = context.getPatch();
		String componentName =  AphroditeUtil.getComponentNameBy(streamService, patch);
		data.put("repository", new LinkResult(componentName, repository.getURL()));
	}

}
