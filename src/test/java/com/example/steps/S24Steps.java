package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Aggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class S24Steps {

    private final InMemoryLegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test data injection via reflection or public setters (since we are in the same package)
    // For this BDD, we instantiate fresh aggregates or modify them if the API allows.
    // The LegacyTransactionRoute has markDualProcessingViolation and markVersioningViolation methods.

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRoute("route-1");
        // Save to repo to simulate lifecycle
        repository.save(aggregate);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        // Context setup, usually handled in the 'When' step via Command construction
    }

    @Given("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        // Context setup
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        // Context setup
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-dual");
        aggregate.markDualProcessingViolation(); // Using helper method defined in the aggregate for testing
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-ver");
        aggregate.markVersioningViolation(); // Using helper method defined in the aggregate for testing
        repository.save(aggregate);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        try {
            // In a real scenario, we might reload from repo:
            // aggregate = repository.findById(aggregate.id()).orElseThrow();
            
            // Construct command. Using reasonable defaults for valid scenario.
            // If the aggregate is in a violation state, the execute() method inside the aggregate should handle it.
            Instant now = Instant.now();
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                aggregate.id(), 
                "rule-123", 
                "MODERN", 
                now
            );
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("routing.updated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // In the domain model, invariants throw IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException
        );
        Assertions.assertTrue(
            capturedException.getMessage().contains("exactly one backend system") || 
            capturedException.getMessage().contains("versioned") ||
            capturedException.getMessage().contains("dual-processing")
        );
    }
}