package com.example.ports;

import com.example.vforce.github.GithubIssue;
import com.example.vforce.shared.ReportDefectCommand;

import java.util.Optional;

/**
 * Port interface for GitHub issue tracking operations.
 * Abstracts the GitHub API interactions (e.g., OkHttp or GitHub Java client).
 */
public interface GithubPort {
    Optional<GithubIssue> createIssue(ReportDefectCommand cmd);
}
