package org.jboss.pull.processor;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Codebase;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.domain.StreamComponent;
import org.jboss.set.aphrodite.spi.NotFoundException;
import org.jboss.set.aphrodite.spi.StreamService;

// this needs to be in the component.
public class AphroditeUtil {

	private static final Pattern RELATED_PR_PATTERN = Pattern.compile(".*github\\.com.*?/([a-zA-Z_0-9-]*)/([a-zA-Z_0-9-]*)/pull.?/(\\d+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ABBREVIATED_RELATED_PR_PATTERN = Pattern.compile("([a-zA-Z_0-9-//]*)#(\\d+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern ABBREVIATED_RELATED_PR_PATTERN_EXTERNAL_REPO = Pattern.compile("([a-zA-Z_0-9-]*)/([a-zA-Z_0-9-]*)#(\\d+)", Pattern.CASE_INSENSITIVE);
	
	public static List<URL> findAllRepositories(StreamService streamService) {
		List<URL> repositories = new ArrayList<URL>();

		List<Stream> streams = streamService.getStreams();
		for (Stream stream : streams) {
			repositories.addAll(findAllRepositoriesInStream(streamService, stream.getName()).stream()
					.filter(e -> !repositories.contains(e))
					.collect(Collectors.toList()));
		}

		return repositories;
	}

	public static List<URL> findAllRepositoriesInStream(
			StreamService streamService, String streamName) {
		return streamService.getStream(streamName).getAllComponents().stream()
				.map((e) -> e.getRepository().getURL())
				.collect(Collectors.<URL> toList());

	}

	public static List<Patch> getPatchesRelatedTo(Aphrodite aphrodite, Patch patch) throws MalformedURLException, URISyntaxException {
		List<Patch> related = new ArrayList<Patch>();
		
		List<URL> urls = getPRFromDescription(patch.getURL(), patch.getDescription());
		for(URL url : urls) {
			try {
				related.add(aphrodite.getPatch(url));
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return related;
	}
	
	public static List<URL> getPRFromDescription(URL url, String content) throws MalformedURLException, URISyntaxException {
		String []paths = url.getPath().split("/");
		Matcher matcher = RELATED_PR_PATTERN.matcher(content);
		List<URL> relatedPullRequests = new ArrayList<URL>();
		while(matcher.find()) {
			if (matcher.groupCount() == 3) {
				URL relatedPullRequest = new URI("https://github.com/" + matcher.group(1) + "/" + matcher.group(2) + "/pulls/" + matcher.group(3) ).toURL();
				relatedPullRequests.add(relatedPullRequest);
//				System.out.println("RELATED PR FOUND: " + relatedPullRequest);
			}
		}
		Matcher abbreviatedMatcher = ABBREVIATED_RELATED_PR_PATTERN.matcher(content);
		while (abbreviatedMatcher.find()) {
			String match = abbreviatedMatcher.group();
			System.out.println("Match: " + match);
			Matcher abbreviatedExternalMatcher = ABBREVIATED_RELATED_PR_PATTERN_EXTERNAL_REPO.matcher(match);
			if (abbreviatedExternalMatcher.find()) {
//				System.out.println("Attempting External Match: " + match);

				if (abbreviatedExternalMatcher.groupCount() == 3) {
					URL relatedPullRequest = new URI("https://github.com/" 
							+ abbreviatedExternalMatcher.group(1) + "/" 
							+ abbreviatedExternalMatcher.group(2) + "/pulls/" 
							+ abbreviatedExternalMatcher.group(3) ).toURL();					
					
//					System.out.println("External Match Found: " + match);
					relatedPullRequests.add(relatedPullRequest);
					continue;
				}
			}
//			System.out.println("Attempting Internal Match: " + match);

			if (abbreviatedMatcher.groupCount() == 2) {
				URL relatedPullRequest = new URI("https://github.com/" + paths[1] + "/" + paths[2] + "/" + "/pulls/" + abbreviatedMatcher.group(2)).toURL();
				relatedPullRequests.add(relatedPullRequest);
//				System.out.println("Internal Match Found: " + relatedPullRequest);
			}
		}
		return relatedPullRequests;
	}

	public static Stream getStreamBy(StreamService streamService, Patch patch) throws NotFoundException{
		URL url = patch.getURL();
		String []paths = url.getPath().split("/");
		URL repo = null;
		try {
			repo = new URI("https://github.com/" + paths[1] + "/" +paths[2]).normalize().toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			e.printStackTrace();
		} 
		
		Codebase codebase = patch.getCodebase();
		
		for(Stream stream : streamService.getStreams()) {
			for(StreamComponent sc : stream.getAllComponents()) {
				if(sc.getRepository().getURL().toString().equals(repo.toString()) && sc.getCodebase().equals(codebase)) {
					return stream;
				}
			}
		}
		
		
		throw new NotFoundException("no stream found for repo " + repo + " -> " + codebase );
	}
}
