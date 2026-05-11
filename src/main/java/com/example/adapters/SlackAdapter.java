package com.example.adapters;

import com.example.ports.SlackPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test") // Only active when 'test' profile is NOT active
public class SlackAdapter implements SlackPort {

    private static final Logger log = LoggerFactory.getLogger(SlackAdapter.class);

    @Override
    public void sendMessage(String channel, String body) {
        // Real implementation would call Slack Web API here.
        // For the purposes of this unit/module test, we just log.
        log.info("[SLACK ADAPTER] Channel: {}, Body: {}", channel, body);
    }
}
