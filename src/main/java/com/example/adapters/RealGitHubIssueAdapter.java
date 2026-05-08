package com.example.adapters;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.validation.model.ReportDefectCmd;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.ports.GithubPort;
import com.example.ports.SlackNotifier;

/**
 * Implementation of the temporal-worker logic or workflow orchestration.
 * This class acts as the glue between the external Temporal trigger and the domain logic.
 * It adapts the incoming trigger into a Domain Command and executes the workflow.
 */
public class RealGitHubIssueAdapter implements AggregateRoot { // Implements AggregateRoot to satisfy compiler if referenced, but mainly a Service wrapper

    private final VForce360Service vForce360Service;
    private final GithubPort githubPort;
    private final SlackNotifier slackNotifier;

    public RealGitHubIssueAdapter(VForce360Service vForce360Service, GithubPort githubPort, SlackNotifier slackNotifier) {
        this.vForce360Service = vForce360Service;
        this.githubPort = githubPort;
        this.slackNotifier = slackNotifier;
    }

    /**
     * Entry point triggered by the Temporal Worker.
     */
    public void reportDefectWorkflow(String validationId, String title, String body) {
        // 1. Reconstitute or Load Aggregate
        ValidationAggregate aggregate = new ValidationAggregate(validationId);

        // 2. Delegate to domain service
        vForce360Service.reportDefect(aggregate, title, body);

        // 3. In a real app, we would persist events here using aggregate.uncommittedEvents()
    }

    @Override
    public java.util.List<com.example.domain.shared.DomainEvent> execute(com.example.domain.shared.Command cmd) {
        // This implementation is purely to satisfy the class definition constraints if extended,
        // though this class is functionally a service adapter.
        throw new com.example.domain.shared.UnknownCommandException(cmd);
    }

    @Override
    public String id() {
        return getClass().getSimpleName();
    }

    @Override
    public int getVersion() {
        return 0;
    }
}