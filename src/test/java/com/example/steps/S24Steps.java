package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.legacy.model.RoutingUpdatedEvent;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S24Steps {

    private Aggregate aggregate;
    private String routeId = "route-123";
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute(routeId);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Data setup for the command, handled in the When step
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Data setup for the command, handled in the When step
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Data setup for the command, handled in the When step
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        var cmd = new UpdateRoutingRuleCmd(
                routeId,
                "rule-abc",
                "VForce360", // Valid target
                Instant.now(),
                1 // Valid version
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals("routing.updated", event.type());
        Assertions.assertEquals("VForce360", event.newTarget());
        Assertions.assertEquals(routeId, event.aggregateId());
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system \(modern or legacy\) to prevent dual-processing\.")
    public void a_legacy_transaction_route_aggregate_that_violates_single_backend() {
        aggregate = new LegacyTransactionRoute(routeId);
        // The violation is triggered by the command data in the When step
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback\.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute(routeId);
        // The violation is triggered by the command data in the When step
    }

    @When("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        // Intentionally empty, the rejection logic depends on which scenario set the state
    }

    @When("the UpdateRoutingRuleCmd command is executed with invalid version")
    public void the_update_routing_rule_cmd_command_is_executed_with_invalid_version() {
        var cmd = new UpdateRoutingRuleCmd(
                routeId,
                "rule-abc",
                "VForce360",
                Instant.now(),
                0 // Invalid version -> violates invariant
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @When("the UpdateRoutingRuleCmd command is executed with invalid target")
    public void the_update_routing_rule_cmd_command_is_executed_with_invalid_target() {
        var cmd = new UpdateRoutingRuleCmd(
                routeId,
                "rule-abc",
                null, // Invalid target -> violates invariant
                Instant.now(),
                1
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_final() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(
            thrownException.getMessage().contains("Routing rules must be versioned") ||
            thrownException.getMessage().contains("exactly one backend system")
        );
    }
}
