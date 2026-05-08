package com.example;

import com.example.adapters.OkHttpSlackClient;
import com.example.adapters.OkHttpGitHubClient;
import com.example.ports.SlackClient;
import com.example.ports.GitHubClient;
import com.example.domain.support.SlackNotificationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public SlackClient slackClient() {
		return new OkHttpSlackClient();
	}

	@Bean
	public GitHubClient gitHubClient() {
		return new OkHttpGitHubClient();
	}

	@Bean
	public SlackNotificationService slackNotificationService(SlackClient slackClient, GitHubClient gitHubClient) {
		return new SlackNotificationService(slackClient, gitHubClient);
	}
}
