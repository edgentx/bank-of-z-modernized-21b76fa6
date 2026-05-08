package com.example.ports;

import com.example.ports.dto.IssueRequest;
import com.example.ports.dto.IssueResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Port for creating issues in GitHub.
 * Used by the Defect reporting workflow.
 */
public interface GitHubIssuePort {
    CompletableFuture<IssueResponse> createIssue(IssueRequest request);
}
