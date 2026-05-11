package com.example.config;

import com.example.adapters.DefaultGitHubAdapter;
import com.example.adapters.DefaultSlackAdapter;
import com.example.domain.defect.service.DefectWorkflowService;
import com.example.mocks.InMemoryDefectRepository;
import com.example.mocks.MockGitHubAdapter;
import com.example.mocks.MockSlackAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefectWorkflowConfig {

    // Primary implementation configuration is handled by ComponentScan on Adapters.
    // This configuration explicitly wires the service for the workflow or test contexts if needed,
    // though @Autowired constructor injection in DefectWorkflowService handles standard cases.
    
    // Note on DefectAggregate: It is a domain object, not a Bean.
    // Note on InMemoryDefectRepository: It is annotated with @Component in the test package.
}