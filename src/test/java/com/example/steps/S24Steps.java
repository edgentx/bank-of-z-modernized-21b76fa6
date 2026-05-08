package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S24Steps {

    private LegacyTransactionRoute aggregate;
    private List<RoutingUpdatedEvent> resultEvents;
    private Exception thrownException;

    // Standard State Setup
    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        this.aggregate = new LegacyTransactionRoute("route-1");
        this.thrownException = null;
    }

    // Command Parameter Setup
    @And("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        // Context: Handled in the When step via construction
    }

    @And("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        // Context: Handled in the When step
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        // Context: Handled in the When step
    }

    // Execution
    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            // Using valid defaults for the 'Success' scenario
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                "route-1", 
                "VForce360", 
                Instant.now().plusSeconds(3600)
            );
            this.resultEvents = aggregate.execute(cmd)
                .stream()
                .filter(e -> e instanceof RoutingUpdatedEvent)
                .map(e -> (RoutingUpdatedEvent) e)
                .toList();
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    // Outcomes
    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected event list, but got null due to potential prior failure");
        assertEquals(1, resultEvents.size());
        assertEquals("routing.updated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

    // Invariant Violation State Setups
    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
        this.aggregate = new LegacyTransactionRoute("route-violation-dual");
        this.aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        this.aggregate = new LegacyTransactionRoute("route-violation-version");
        this.aggregate.markVersioningViolation();
    }
}
