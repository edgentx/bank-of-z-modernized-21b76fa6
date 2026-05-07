package com.example.config;

import com.example.ports.SlackPort;
import com.example.adapters.SlackAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Bean
    public SlackPort slackPort() {
        // In a real environment, this might be conditionally instantiated
        // based on a profile (e.g., using MockSlackAdapter for 'test' and
        // SlackAdapter for 'prod'). For this implementation, we provide the real adapter.
        return new SlackAdapter();
    }
}
