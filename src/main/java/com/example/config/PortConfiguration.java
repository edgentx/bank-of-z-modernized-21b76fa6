package com.example.config;

import com.example.adapters.GitHubIssueAdapter;
import com.example.adapters.SlackNotificationAdapter;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class PortConfiguration {

    // We do not define Mock beans here manually to avoid class not found errors in main source.
    // The Spring Boot Test context will pick up the Mock implementations from the test classpath
    // if they are defined in @SpringBootTest setup, or we can rely on @MockBean.
    
    // Leaving this file empty as the Main adapters are auto-scanned via @Component.
}
