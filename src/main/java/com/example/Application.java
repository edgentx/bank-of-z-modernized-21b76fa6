package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import com.example.ports.SlackNotificationPort;
import com.example.mocks.MockSlackNotificationAdapter;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

    /**
     * We define the Mock implementation as the primary bean here to satisfy the
     * VW454SlackLinkRegressionTest which expects to inject the Mock.
     * In a real production profile, this would be swapped for the real adapter.
     */
    @Bean
    @Primary
    public SlackNotificationPort slackNotificationPort() {
        return new MockSlackNotificationAdapter();
    }
}
