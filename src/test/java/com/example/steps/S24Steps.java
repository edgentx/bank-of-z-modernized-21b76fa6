package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Scenarios Setup ---

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Parameter injection handled in When step
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Parameter injection handled in When step
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Parameter injection handled in When step
    }

    // --- Dual Processing Invariant ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_route_aggregate_that_violates_single_processing() {
        aggregate = new LegacyTransactionRoute("route-bad-1");
        aggregate.markDualProcessingViolation();
    }

    // --- Versioning Invariant ---

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-bad-2");
        aggregate.markVersioningViolation();
    }

    // --- Action ---

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            // Default valid command data if not specified otherwise
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                aggregate.id(),
                "rule-456", 
                "MODERN", 
                Instant.now(),
                2
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Outcomes ---

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);

        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("route-123", event.aggregateId());
        assertEquals("MODERN", event.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Invariant violations typically throw IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
