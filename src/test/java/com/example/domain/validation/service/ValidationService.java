package com.example.domain.validation.service;

import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.repository.ValidationRepository;
import com.example.ports.SlackPort;

public class ValidationService {
    private final ValidationRepository repository;
    private final SlackPort slackPort;

    public ValidationService(ValidationRepository repository, SlackPort slackPort) {
        this.repository = repository;
        this.slackPort = slackPort;
    }

    public void reportDefect(ReportDefectCmd cmd) {
        ValidationAggregate aggregate = new ValidationAggregate(cmd.validationId());
        aggregate.execute(cmd); // Raises event if successful
        repository.save(aggregate);

        // Side effect: Call external Slack adapter
        // This is the behavior we are testing: that the URL reaches here
        slackPort.sendDefectNotification(cmd.summary(), cmd.githubIssueUrl());
    }
}
