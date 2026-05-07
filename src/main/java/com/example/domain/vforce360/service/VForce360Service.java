package com.example.domain.vforce360.service;

import com.example.adapters.GithubIssueAdapter;
import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.domain.vforce360.model.ReportDefectCmd;
import com.example.domain.vforce360.model.VForce360Aggregate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service handling VForce360 domain logic.
 * S-FB-1: Orchestrates defect reporting to GitHub/Slack.
 */
@Service
public class VForce360Service {

    private final GithubIssueAdapter githubAdapter;

    public VForce360Service(GithubIssueAdapter githubAdapter) {
        this.githubAdapter = githubAdapter;
    }

    /**
     * Handles the ReportDefect command, executes aggregate logic,
     * and triggers the external integration (GitHub).
     */
    public String handleReportDefect(ReportDefectCmd cmd) {
        VForce360Aggregate aggregate = new VForce360Aggregate(cmd.defectId());
        
        // Execute domain logic
        List<DefectReportedEvent> events = aggregate.execute(cmd);
        
        if (!events.isEmpty()) {
            DefectReportedEvent event = events.get(0);
            
            // Call adapter to create the issue and get the URL
            String url = githubAdapter.createIssue(event);
            
            // In a full CQRS implementation, we would persist events here.
            // For S-FB-1, we return the URL for validation/Slack notification.
            return url;
        }
        
        throw new RuntimeException("Failed to report defect");
    }
}
