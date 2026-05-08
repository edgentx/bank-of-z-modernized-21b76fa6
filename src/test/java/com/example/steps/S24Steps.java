package com.example.steps;

import com.example.domain.legacybridge.model.*;
import com.example.domain.legacybridge.repository.LegacyTransactionRouteRepository;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    // System Under Test
    private LegacyTransactionRoute aggregate;
    private final LegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();

    // Inputs
    private String inputRuleId;
    private String inputNewTarget;
    private Instant inputEffectiveDate;

    // Outputs
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        this.aggregate = new LegacyTransactionRoute("route-123");
    }

    @And("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        this.inputRuleId = "rule-abc";
    }

    @And("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        this.inputNewTarget = "MODERN";
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        this.inputEffectiveDate = Instant.now().plusSeconds(3600);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
            aggregate.id(),
            inputRuleId,
            inputNewTarget,
            inputEffectiveDate
        );

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultingEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultingEvents.get(0);
        Assertions.assertTrue(event instanceof RoutingUpdatedEvent, "Event should be RoutingUpdatedEvent");
        
        RoutingUpdatedEvent routingEvent = (RoutingUpdatedEvent) event;
        Assertions.assertEquals("routing.updated", routingEvent.type());
        Assertions.assertEquals(inputRuleId, routingEvent.ruleId());
        Assertions.assertEquals(inputNewTarget, routingEvent.newTarget());
        Assertions.assertEquals(inputEffectiveDate, routingEvent.effectiveDate());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        this.aggregate = new LegacyTransactionRoute("route-bad-dual");
        // Mark the aggregate state such that execution triggers the invariant check
        // Assuming existence of a state setter or previous command that sets this up.
        // Based on the domain model, the aggregate might need to be in a state where it detects the conflict.
        // We will simulate this by setting a boolean flag if the model supports it, or by modifying the command inputs if the check depends on input.
        // Since the previous files showed specific methods like markDualProcessingViolation, we use that.
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        this.aggregate = new LegacyTransactionRoute("route-bad-ver");
        aggregate.markVersioningViolation();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for domain error types (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected a domain exception, got: " + capturedException.getClass().getSimpleName()
        );
    }
}
