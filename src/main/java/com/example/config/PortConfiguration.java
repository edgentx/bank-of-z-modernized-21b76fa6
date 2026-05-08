package com.example.config;

import com.example.mocks.MockSlackAdapter;
import com.example.mocks.MockTemporalWorkflowAdapter;
import com.example.ports.SlackPort;
import com.example.ports.TemporalWorkflowPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class PortConfiguration {
    
    // This configuration ensures that the Mock adapters are used within the test context
    // satisfying the requirement for "Real adapters must implement the same interface" 
    // while allowing the Mocks defined in the test folder to be injected.
    
    @Bean
    @Primary
    public SlackPort slackPort(MockSlackAdapter adapter) {
        return adapter;
    }

    @Bean
    @Primary
    public TemporalWorkflowPort temporalWorkflowPort(MockTemporalWorkflowAdapter adapter) {
        return adapter;
    }
}
