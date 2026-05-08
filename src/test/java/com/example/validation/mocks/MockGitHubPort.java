package com.example.validation.mocks;

import com.example.validation.domain.model.DefectReport;
import com.example.validation.domain.model.GitHubIssueLink;
import com.example.validation.ports.GitHubPort;

public class MockGitHubPort implements GitHubPort {
    private boolean createIssueCalled = false;
    private DefectReport lastReceivedReport;
    private GitHubIssueLink linkToReturn = new GitHubIssueLink("https://github.com/test/repo/issues/1");

    @Override
    public GitHubIssueLink createIssue(DefectReport report) {
        this.createIssueCalled = true;
        this.lastReceivedReport = report;
        return linkToReturn;
    }

    public void setLinkToReturn(String url) {
        this.linkToReturn = new GitHubIssueLink(url);
    }

    // Verification helpers
    public boolean wasCreateIssueCalled() {
        return createIssueCalled;
    }
    
    public DefectReport getLastReceivedReport() {
        return lastReceivedReport;
    }
}
