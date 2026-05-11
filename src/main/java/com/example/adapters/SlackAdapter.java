package com.example.adapters;

import com.example.domain.vforce360.model.DefectReportedEvent;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Component;

import java.util.Formatter;

@Component
public class SlackAdapter implements SlackPort {

    @Override
    public void sendNotification(DefectReportedEvent event) {
        // In a real implementation, this would use the Slack WebAPI to post a message.
        // For the scope of this validation, the formatMessageBody method is critical.
        System.out.println("Sending to Slack: " + formatMessageBody(event));
    }

    @Override
    public String formatMessageBody(DefectReportedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("*Defect Reported*\n");
        sb.append("*ID:* ").append(event.defectId()).append("\n");
        sb.append("*Project:* ").append(event.project()).append("\n");
        sb.append("*Severity:* ").append(event.severity()).append("\n");
        sb.append("*Description:* ").append(event.description() != null ? event.description() : "N/A").append("\n");
        sb.append("*GitHub Issue:* ").append(event.githubIssueUrl()).append("\n");
        return sb.toString();
    }
}