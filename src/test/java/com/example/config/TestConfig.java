package com.example.config;

import com.example.ports.SlackNotifier;
import com.example.domain.vforce360.service.VForce360Service;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@TestConfiguration
public class TestConfig {

    @Bean
    public SlackNotifier mockSlackNotifier() {
        // Create a mock that does nothing by default (Red Phase)
        SlackNotifier mock = Mockito.mock(SlackNotifier.class);
        doNothing().when(mock).send(anyString());
        return mock;
    }

    @Bean
    public VForce360Service vForce360Service(SlackNotifier slackNotifier) {
        return new VForce360Service(slackNotifier);
    }
}