package com.example.infrastructure.github;

import com.example.domain.defect.model.ReportDefectCmd;
import com.example.infrastructure.config.GitHubProperties;
import com.example.ports.GitHubPort;
import org.springframework.stereotype.Service;

@Service
public class GitHubIssueService {

    private final GitHubPort gitHubPort;
    private final GitHubProperties properties;

    public GitHubIssueService(GitHubPort gitHubPort, GitHubProperties properties) {
        this.gitHubPort = gitHubPort;
        this.properties = properties;
    }

    public String createIssue(ReportDefectCmd cmd) {
        return gitHubPort.createIssue(cmd.title(), cmd.description());
    }
}
