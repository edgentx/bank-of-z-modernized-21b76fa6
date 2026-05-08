package com.example.workflows;

import com.example.domain.validation.model.LinkGitHubIssueCmd;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ReportDefectActivity implements ReportDefectWorkflow {

    @Autowired
    private ValidationRepository validationRepository;

    @Override
    public String reportAndLinkDefect(String validationId, String summary, String description, String severity, String githubUrl) {
        ValidationAggregate aggregate = validationRepository.findById(validationId)
                .orElse(new ValidationAggregate(validationId));

        // 1. Report Defect
        if (!aggregate.isReported()) {
            aggregate.execute(new ReportDefectCmd(validationId, summary, description, severity));
            validationRepository.save(aggregate);
        }

        // 2. Link GitHub Issue (The fix for VW-454)
        if (aggregate.getGithubIssueUrl() == null && githubUrl != null) {
            aggregate.execute(new LinkGitHubIssueCmd(validationId, githubUrl));
            validationRepository.save(aggregate);
        }

        return aggregate.getGithubIssueUrl();
    }
}

@ActivityInterface
interface ReportDefectWorkflow {
    @ActivityMethod
    String reportAndLinkDefect(String validationId, String summary, String description, String severity, String githubUrl);
}
