package com.example.domain.defect.service;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.shared.DomainEvent;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application Service for handling Defect reporting commands.
 * Bridges the Temporal/REST layer with the Domain Aggregate.
 */
@Service
public class DefectService {

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public DefectService(SlackPort slackPort, GitHubPort gitHubPort) {
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    public List<DomainEvent> reportDefect(String defectId, String channelId) {
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, channelId);
        DefectAggregate aggregate = new DefectAggregate(defectId, slackPort, gitHubPort);
        return aggregate.execute(cmd);
    }
}
