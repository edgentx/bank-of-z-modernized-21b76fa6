package com.example.config;

import com.example.adapters.SlackNotificationAdapter;
import com.example.adapters.VForce360Adapter;
import com.example.mocks.MockSlackNotificationPort;
import com.example.mocks.MockVForce360Port;
import com.example.ports.SlackNotificationPort;
import com.example.ports.VForce360Port;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortConfiguration {

    @Bean
    @ConditionalOnProperty(name = "adapters.vforce360.mock", havingValue = "true", matchIfMissing = true)
    public VForce360Port mockVForce360Port() {
        return new MockVForce360Port();
    }

    @Bean
    @ConditionalOnProperty(name = "adapters.vforce360.mock", havingValue = "false")
    public VForce360Port realVForce360Port() {
        return new VForce360Adapter();
    }

    @Bean
    @ConditionalOnProperty(name = "adapters.slack.mock", havingValue = "true", matchIfMissing = true)
    public SlackNotificationPort mockSlackNotificationPort() {
        return new MockSlackNotificationPort();
    }

    @Bean
    @ConditionalOnProperty(name = "adapters.slack.mock", havingValue = "false")
    public SlackNotificationPort realSlackNotificationPort() {
        return new SlackNotificationAdapter();
    }
}
