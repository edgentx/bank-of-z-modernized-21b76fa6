package com.example;

import com.example.ports.VForce360Port;
import org.springframework.stereotype.Service;

/**
 * Service Gateway for VForce360 Defect Reporting.
 * Acts as the entry point for Temporal workflow activities or schedulers.
 * Orchestrates the creation of the defect payload and invokes the port.
 */
@Service
public class VForce360ServiceGateway {

    private final VForce360Port vForce360Port;
    private final GitHubUrlProvider gitHubUrlProvider;

    public VForce360ServiceGateway(VForce360Port vForce360Port, GitHubUrlProvider gitHubUrlProvider) {
        this.vForce360Port = vForce360Port;
        this.gitHubUrlProvider = gitHubUrlProvider;
    }

    /**
     * Triggers the defect reporting workflow.
     * Constructs the message body ensuring the GitHub URL is present
     * and delegates to the port.
     *
     * @param defectId The unique ID of the defect (e.g., VW-454).
     * @param description The description of the defect.
     */
    public void reportDefectViaTemporal(String defectId, String description) {
        if (defectId == null || defectId.isBlank()) {
            throw new IllegalArgumentException("defectId cannot be null or blank");
        }

        // 1. Resolve the GitHub URL for the specific defect
        String url = gitHubUrlProvider.getIssueUrl(defectId);

        // 2. Construct the body for Slack
        // Requirement: "Slack body includes GitHub issue: <url>"
        String body = String.format(
            "Defect Reported: %s\nDescription: %s\nGitHub issue: %s",
            defectId,
            description != null ? description : "No description provided",
            url
        );

        // 3. Send via the port
        vForce360Port.reportDefect(defectId, body);
    }
}
