package com.example.steps;

import com.example.mocks.MockSlackPort;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration and runner for S-FB-1.
 * This class sets up the Spring context for Cucumber tests, providing the Mock implementations.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = SFB1TestSuite.TestConfig.class)
public class SFB1TestSuite {

    @Configuration
    static class TestConfig {
        
        @Bean
        @Primary
        public MockSlackPort mockSlackPort() {
            return new MockSlackPort();
        }
    }
}
