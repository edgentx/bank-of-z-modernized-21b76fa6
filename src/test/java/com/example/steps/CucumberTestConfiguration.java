package com.example.steps;

import com.example.mocks.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CucumberTestConfiguration {
    @Bean
    public TellerSessionRepository tellerSessionRepository() {
        return new InMemoryTellerSessionRepository();
    }
}
