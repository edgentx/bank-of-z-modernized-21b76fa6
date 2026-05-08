package com.example.infrastructure.adapters.github;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.example.infrastructure.config.GitHubFeignConfig;

/**
 * Feign Client interface for GitHub Issues API.
 */
@FeignClient(
    name = "github-issue-client",
    url = "https://api.github.com",
    configuration = GitHubFeignConfig.class
)
public interface GitHubIssueClient {
    
    @PostMapping("/repos/{owner}/{repo}/issues")
    GitHubIssueResponse createIssue(
        // Feign expands these automatically based on path variables in the call
        String owner, 
        String repo, 
        @RequestBody GitHubIssueRequest request
    );
}
