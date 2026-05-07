package com.example.adapters;

import com.example.ports.GitHubPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real implementation of the GitHub Port.
 * In a production environment, this would use the GitHub REST API client.
 */
@Component
public class GitHubAdapter implements GitHubPort {

    private static final Logger log = LoggerFactory.getLogger(GitHubAdapter.class);

    @Override
    public String createIssue(String title, String body) {
        // Real implementation would go here (e.g., WebClient.post()...)
        log.info("[GITHUB_ADAPTER] Creating issue title={}", title);

        // Simulating a URL return for valid operation flow
        String mockUrl = "https://github.com/example/project/issues/" + UUID.randomUUID().toString().substring(0, 4);
        
        // Pseudo-code for actual GitHub call:
        // return webClient.post()
        //     .uri("/repos/{owner}/{repo}/issues")
        //     .bodyValue(Map.of("title", title, "body", body))
        //     .retrieve()
        //     .bodyToMono(JsonNode.class)
        //     .map(node -> node.get("html_url").asText())
        //     .block();
        
        return mockUrl;
    }
}
