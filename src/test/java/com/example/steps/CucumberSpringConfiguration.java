package com.example.steps;

import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CucumberSpringConfiguration {

    @Bean
    public TellerSessionRepository tellerSessionRepository() {
        return new InMemoryTellerSessionRepository();
    }
}
