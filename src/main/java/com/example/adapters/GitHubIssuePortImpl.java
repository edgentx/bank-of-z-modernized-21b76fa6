package com.example.adapters;

import com.example.domain.shared.ReportDefectCmd;
import com.example.ports.GitHubIssuePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class GitHubIssuePortImpl implements GitHubIssuePort {

    private static final Logger log = LoggerFactory.getLogger(GitHubIssuePortImpl.class);
    private final RestClient restClient;
    private final String apiUrl;
    private final String authToken;
    private final String repoOwner;
    private final String repoName;

    public GitHubIssuePortImpl(RestClient.Builder restClientBuilder,
                               @Value("${github.api.url}") String apiUrl,
                               @Value("${github.auth.token}") String authToken,
                               @Value("${github.repo.owner}") String repoOwner,
                               @Value("${github.repo.name}") String repoName) {
        this.restClient = restClientBuilder.build();
        this.apiUrl = apiUrl;
        this.authToken = authToken;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    @Override
    public String createIssue(ReportDefectCmd cmd) {
        String url = "%s/repos/%s/%s/issues".formatted(apiUrl, repoOwner, repoName);

        Map<String, Object> payload = new HashMap<>();
        payload.put("title", cmd.title());
        payload.put("body", formatBody(cmd));
        if (cmd.metadata() != null && cmd.metadata().containsKey("labels")) {
            payload.put("labels", cmd.metadata().get("labels"));
        }

        try {
            GitHubIssueResponse response = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + authToken)
                    .header("Accept", "application/vnd.github+json")
                    .body(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new RuntimeException("Client error creating issue: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw new RuntimeException("Server error creating issue: " + res.getStatusCode());
                    })
                    .body(GitHubIssueResponse.class);

            if (response != null && response.htmlUrl() != null) {
                log.info("GitHub issue created: {}", response.htmlUrl());
                return response.htmlUrl();
            } else {
                throw new RuntimeException("Failed to create issue: No URL returned");
            }
        } catch (Exception e) {
            log.error("Error creating GitHub issue", e);
            throw new RuntimeException("Failed to create GitHub issue", e);
        }
    }

    private String formatBody(ReportDefectCmd cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append(cmd.description() != null ? cmd.description() : "No description");
        sb.append("\n\n---");
        sb.append("\n**Severity:** ").append(cmd.severity());
        sb.append("\n**Component:** ").append(cmd.component() != null ? cmd.component() : "Unknown");
        sb.append("\n**Project:** ").append(cmd.metadata() != null ? cmd.metadata().get("project") : "N/A");
        return sb.toString();
    }

    private record GitHubIssueResponse(String htmlUrl, int id) {}
}
