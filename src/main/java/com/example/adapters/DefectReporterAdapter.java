package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter for DefectReporterPort.
 * Formats the Slack message body with the GitHub URL and delegates to SlackPort.
 */
public class DefectReporterAdapter implements com.example.ports.DefectReporterPort {

    private static final Logger log = LoggerFactory.getLogger(DefectReporterAdapter.class);
    private final SlackPort slackPort;

    public DefectReporterAdapter(SlackPort slackPort) {
        this.slackPort = slackPort;
    }

    @Override
    public void reportDefect(String channelId, String url) {
        log.info("Reporting defect for channel {}: {}", channelId, url);
        
        // Formatting URL as Slack link: <url|text> or just <url>
        // VW-454 requires the link to be present in the body.
        String formattedBody = String.format("New issue reported: <%s|View Issue>", url);
        
        slackPort.postMessage(channelId, formattedBody);
    }
}
