package com.example.domain.validation;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.domain.defect.port.DefectRepository;
import com.example.ports.GitHubPort;
import com.example.ports.SlackPort;
import io.temporal.spring.boot.WorkflowImpl;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@WorkflowImpl(taskQueue = "DEFECT_REPORTING_TASK_QUEUE")
public class DefectReportingWorkflow implements DefectReportingWorkflowInterface {

    private final DefectRepository defectRepository;
    private final GitHubPort gitHubPort;
    private final SlackPort slackPort;

    @Autowired
    public DefectReportingWorkflow(DefectRepository defectRepository, GitHubPort gitHubPort, SlackPort slackPort) {
        this.defectRepository = defectRepository;
        this.gitHubPort = gitHubPort;
        this.slackPort = slackPort;
    }

    @Override
    public String reportDefect(ReportDefectCommand command) {
        // 1. Create GitHub Issue
        String issueUrl = gitHubPort.createIssue(command.description());
        
        // 2. Update Command with URL (simulate immutable command update)
        var cmdWithUrl = new ReportDefectCommand(command.defectId(), command.description(), issueUrl);

        // 3. Process Domain Logic
        DefectAggregate aggregate = defectRepository.findById(command.defectId())
                .orElse(new DefectAggregate(command.defectId()));
        
        aggregate.execute(cmdWithUrl);
        defectRepository.save(aggregate);

        // 4. Notify Slack
        slackPort.sendNotification(issueUrl);

        return issueUrl;
    }
}

@WorkflowInterface
interface DefectReportingWorkflowInterface {
    @WorkflowMethod
    String reportDefect(ReportDefectCommand command);
}