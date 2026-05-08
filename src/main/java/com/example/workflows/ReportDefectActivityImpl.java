package com.example.workflows;

import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCmd;
import com.example.domain.defect.repository.DefectRepository;
import com.example.ports.SlackNotificationPort;
import org.springframework.stereotype.Component;

@Component
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private final DefectRepository defectRepository;
    private final SlackNotificationPort slackNotificationPort;

    public ReportDefectActivityImpl(DefectRepository defectRepository, SlackNotificationPort slackNotificationPort) {
        this.defectRepository = defectRepository;
        this.slackNotificationPort = slackNotificationPort;
    }

    @Override
    public void reportDefect(String defectId, String summary, String githubUrl) {
        DefectAggregate aggregate = new DefectAggregate(defectId);
        ReportDefectCmd cmd = new ReportDefectCmd(defectId, summary, githubUrl);
        
        aggregate.execute(cmd);
        defectRepository.save(aggregate);

        DefectAggregate savedDefect = defectRepository.findById(defectId);
        if (savedDefect != null) {
            String message = String.format(
                "Defect Reported: %s\n" +
                "Details: %s\n" +
                "GitHub Issue: <%s|View>",
                savedDefect.id(),
                "GitHub URL in Slack body (end-to-end)",
                savedDefect.getGithubUrl()
            );
            slackNotificationPort.sendNotification("#vforce360-issues", message);
        }
    }
}
