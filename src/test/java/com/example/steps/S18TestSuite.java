package com.example.steps;

import com.example.mocks.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@CucumberContextConfiguration
@SpringBootTest(classes = S18TestSuite.TestConfig.class)
public class S18TestSuite {

    @Configuration
    static class TestConfig {
        @Bean
        public TellerSessionRepository tellerSessionRepository() {
            return new InMemoryTellerSessionRepository();
        }
    }
}
