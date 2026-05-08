package com.example.adapters;

import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.model.command.ReportDefectCmd;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.SlackNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporalWorkflowAdapter {
    private static final Logger log = LoggerFactory.getLogger(TemporalWorkflowAdapter.class);
    private final ValidationRepository repository;
    private final SlackNotifier slackNotifier;

    public TemporalWorkflowAdapter(ValidationRepository repository, SlackNotifier slackNotifier) {
        this.repository = repository;
        this.slackNotifier = slackNotifier;
    }

    public void reportDefect(String id, String url, String severity, String component) {
        // 1. Load Aggregate
        ValidationAggregate aggregate = repository.findById(id)
                .orElseGet(() -> new ValidationAggregate(id));

        // 2. Execute Command
        ReportDefectCmd cmd = new ReportDefectCmd(id, url, severity, component);
        var events = aggregate.execute(cmd);

        // 3. Side Effects (Slack Notification)
        // Requirement: Verify Slack body contains GitHub issue link
        if (!events.isEmpty()) {
            String message = String.format("Defect Reported: GitHub issue <%s|Link> for %s", url, id);
            slackNotifier.send(message);
        }

        // 4. Persist
        repository.save(aggregate);
    }
}
