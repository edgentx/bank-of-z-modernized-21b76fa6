package com.example.steps;

import com.example.mocks.MockSlackNotificationPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Base configuration for Cucumber tests ensuring Spring context is loaded
 * and Mocks are available.
 */
@SpringBootTest
@Import(MockSlackNotificationPort.class)
public class CucumberSpringConfiguration {
}
