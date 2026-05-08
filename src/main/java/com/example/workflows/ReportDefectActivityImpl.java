package com.example.workflows;

import com.example.adapters.DefectRepositoryAdapter;
import com.example.adapters.GitHubAdapter;
import com.example.adapters.SlackAdapter;
import com.example.domain.defect.model.DefectAggregate;
import com.example.domain.defect.model.ReportDefectCommand;
import com.example.ports.DefectPort;
import com.example.ports.NotificationPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementation of the ReportDefectActivity.
 * Orchestrates the Aggregate, Repository, and external Adapters.
 */
@Component
public class ReportDefectActivityImpl implements ReportDefectActivity {

    private final DefectRepositoryAdapter defectRepository;
    private final DefectPort gitHubAdapter;
    private final NotificationPort slackAdapter;

    public ReportDefectActivityImpl(DefectRepositoryAdapter defectRepository, 
                                    GitHubAdapter gitHubAdapter, 
                                    SlackAdapter slackAdapter) {
        this.defectRepository = defectRepository;
        this.gitHubAdapter = gitHubAdapter;
        this.slackAdapter = slackAdapter;
    }

    @Override
    public String generateId() {
        return "d-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public void saveDefect(String id, String title, String description) {
        DefectAggregate aggregate = new DefectAggregate(id);
        // Execute command to validate state transition and generate events
        aggregate.execute(new ReportDefectCommand(id, title, description));
        defectRepository.save(aggregate);
    }

    @Override
    public String createGitHubIssue(String title, String description) {
        // Delegate to the GitHub Adapter
        return gitHubAdapter.createExternalTicket(title, description);
    }

    @Override
    public void notifySlack(String defectId, String githubUrl) {
        // Delegate to the Slack Adapter
        // This ensures the defect ID and the returned URL are passed together
        slackAdapter.sendNotification(defectId, githubUrl);
    }
}
