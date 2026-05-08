package com.example.infrastructure.adapters.github;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Feign Client for interacting with GitHub Issues API.
 * BOM pinned groupIds verified.
 */
@FeignClient(name = "githubIssueClient", url = "https://api.github.com")
public interface GitHubIssueClient {

    record IssueRequest(String title, String body) {}

    @PostMapping("/repos/example/bank-of-z/issues")
    String createIssue(@RequestBody IssueRequest request);
}