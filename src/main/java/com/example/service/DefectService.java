package com.example.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.ports.SlackNotifierPort;
import org.springframework.stereotype.Service;

/**
 * Service for handling defect reporting workflow.
 * This is the implementation file that will cause the tests to fail initially.
 */
@Service
public class DefectService {

    private final SlackNotifierPort slackNotifier;

    public DefectService(SlackNotifierPort slackNotifier) {
        this.slackNotifier = slackNotifier;
    }

    /**
     * Orchestrates the reporting of a defect.
     * 1. Execute Command on Aggregate
     * 2. Notify Slack via Port
     * 
     * @param cmd The command to report a defect
     * @return The ID of the generated GitHub issue
     */
    public String reportDefect(ReportDefectCmd cmd) {
        // RED PHASE IMPLEMENTATION STUB
        // This is intentionally incorrect or incomplete to verify test failures.
        
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        
        // Execute domain logic
        var events = aggregate.execute(cmd);
        
        if (!events.isEmpty()) {
            var event = events.get(0);
            
            // INTENTIONAL BUG (Simulating VW-454):
            // The notification logic currently forgets to append the GitHub URL from the event.
            String slackMessage = "Defect Reported: " + cmd.title(); 
            // Missing: " See: " + event.githubUrl();
            
            slackNotifier.send(slackMessage);
            
            // Returning null/empty to force assertion failures downstream
            return null; 
        }
        
        throw new IllegalStateException("Failed to report defect");
    }
}
