package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.validation.model.ValidationAggregate;
import com.example.domain.validation.ReportDefectCmd;
import com.example.ports.SlackPort;
import com.example.ports.GitHubPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper class to bridge Cucumber steps and the domain logic.
 * In a real app, this might be a CommandDispatcher or ApplicationService.
 */
@Component
public class AggregateTestHelper {

    private final SlackPort slackPort;
    private final GitHubPort gitHubPort;

    public AggregateTestHelper(SlackPort slackPort, GitHubPort gitHubPort) {
        // Spring injects the Mock implementations automatically based on component scan
        // or explicit @Primary configuration in test scope.
        this.slackPort = slackPort;
        this.gitHubPort = gitHubPort;
    }

    public List<DomainEvent> executeCommand(Command cmd) {
        // This simulates the repository loading the aggregate.
        // For this defect fix, we are essentially testing a new use case on the validation aggregate.
        // We assume the aggregate ID matches the defect ID for simplicity.
        String aggregateId = "validation-aggregate-1";
        
        ValidationAggregate aggregate = new ValidationAggregate(aggregateId);
        
        // Inject dependencies (Ports) into the Aggregate
        // In a pure Domain model, ports might be injected via the Command handler or
        // the Aggregate might hold a reference if it's a stateful service.
        // Here we assume the Aggregate needs the SlackPort to fulfill the command.
        aggregate.setSlackPort(slackPort);
        aggregate.setGitHubPort(gitHubPort);

        return aggregate.execute(cmd);
    }

    public SlackPort getSlackPort() {
        return slackPort;
    }
}
