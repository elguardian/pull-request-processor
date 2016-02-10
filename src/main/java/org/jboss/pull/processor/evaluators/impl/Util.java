package org.jboss.pull.processor.evaluators.impl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;

public final class Util {
	private Util() {}
	
	private static final Pattern pattern = Pattern.compile("[0-9]\\.[0-9]\\.[0-9z]");
	
	public static String getStreams(Issue issue) {
		
		EnumSet<FlagStatus> set = EnumSet.of(FlagStatus.ACCEPTED, FlagStatus.SET);
		List<String> streams = new ArrayList<>();
		Map<String, FlagStatus> statuses = issue.getStreamStatus();
		for(Map.Entry<String, FlagStatus> status : statuses.entrySet()) {
			String stream = extract(status.getKey());
			if(set.contains(status.getValue()) && stream != null) {
				streams.add(status.getKey());
			}
		}
		
		if(!streams.isEmpty()) {
			String stream = streams.get(0);
			return "(" + (stream.length() >= 5 ? stream.substring(stream.length() - 5) : stream) + ")";
		} else {
			return "(N/A)";
		}
	}
	
	public static String extract(String value) {
		Matcher matcher = pattern.matcher(value);
		return (matcher.find()) ? matcher.group() : null;
	}
}
