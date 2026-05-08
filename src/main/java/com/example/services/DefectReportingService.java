package com.example.services;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.GitHubIssuePort;
import com.example.ports.SlackNotifierPort;
import com.example.domain.shared.DomainEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefectReportingService {

    private final DefectRepository defectRepository;
    private final GitHubIssuePort gitHubIssuePort;
    private final SlackNotifierPort slackNotifierPort;

    public DefectReportingService(DefectRepository defectRepository, 
                                   GitHubIssuePort gitHubIssuePort, 
                                   SlackNotifierPort slackNotifierPort) {
        this.defectRepository = defectRepository;
        this.gitHubIssuePort = gitHubIssuePort;
        this.slackNotifierPort = slackNotifierPort;
    }

    public void reportDefect(ReportDefectCmd cmd) {
        DefectAggregate aggregate = new DefectAggregate(cmd.defectId());
        List<DomainEvent> events = aggregate.execute(cmd);
        
        // defectRepository.save(aggregate);
        
        events.forEach(event -> {
            // String url = gitHubIssuePort.createIssue(...).orElse(null);
            // slackNotifierPort.notify(event, "Body with URL: " + url);
        });
    }
}
