package com.example.validation.ports;

import com.example.validation.domain.model.GitHubIssueLink;

public interface SlackPort {
    void sendNotification(GitHubIssueLink link);
}
