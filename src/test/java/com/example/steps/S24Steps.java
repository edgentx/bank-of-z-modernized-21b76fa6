package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.RoutingRuleUpdatedEvent;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Handled in context by the When step
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Handled in context by the When step
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Handled in context by the When step
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            var cmd = new UpdateRoutingRuleCmd(
                    "route-123",
                    "rule-abc",
                    "ModernSystem",
                    Instant.now()
            );
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof RoutingRuleUpdatedEvent);

        var event = (RoutingRuleUpdatedEvent) resultingEvents.get(0);
        assertEquals("route-123", event.aggregateId());
        assertEquals("rule-abc", event.ruleId());
        assertEquals("ModernSystem", event.newTarget());
        assertEquals("RoutingRuleUpdated", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_single_target() {
        aggregate = new LegacyTransactionRoute("route-bad-dual");
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-bad-version");
        aggregate.markVersioningViolation();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Checking for RuntimeException or specific Invariant violations
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
