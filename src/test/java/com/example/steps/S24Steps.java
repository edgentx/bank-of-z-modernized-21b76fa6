package com.example.steps;

import com.example.domain.routing.model.LegacyTransactionRoute;
import com.example.domain.routing.model.RoutingRuleUpdatedEvent;
import com.example.domain.routing.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-24: UpdateRoutingRuleCmd.
 * Tests the domain logic directly without HTTP or Database.
 */
public class S24Steps {

    // Test State
    private LegacyTransactionRoute aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Test Data Constants
    private static final String VALID_ROUTE_ID = "route-legacy-mainframe";
    private static final String VALID_RULE_ID = "rule-deposit-001";
    private static final String TARGET_MODERN = "MODERN";
    private static final String TARGET_LEGACY = "LEGACY";

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_Legacy_Transaction_Route_aggregate() {
        this.aggregate = new LegacyTransactionRoute(VALID_ROUTE_ID);
    }

    @Given("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        // Context setup - usually handled in the 'When' step via parameters
        // Implicitly satisfied by using constant VALID_RULE_ID
    }

    @Given("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        // Implicitly satisfied by using constant TARGET_MODERN
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        // Implicitly satisfied by using Instant.now()
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_Update_Routing_Rule_Cmd_command_is_executed() {
        executeCommand(VALID_RULE_ID, TARGET_MODERN, Instant.now().plusSeconds(3600));
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof RoutingRuleUpdatedEvent, "Expected RoutingRuleUpdatedEvent");
    }

    // --- Invariant Violation Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_single_backend() {
        this.aggregate = new LegacyTransactionRoute(VALID_ROUTE_ID);
        // The violation here is attempting to set a target that is NOT modern or legacy,
        // or potentially trying to route to both (though this command is single-target).
        // We simulate the "When" step later with a bad target.
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_legacy_transaction_route_aggregate_that_violates_versioning() {
        this.aggregate = new LegacyTransactionRoute(VALID_ROUTE_ID);
        // Seed with an initial rule version
        UpdateRoutingRuleCmd seedCmd = new UpdateRoutingRuleCmd(
            VALID_ROUTE_ID, 
            "rule-rollback-test", 
            TARGET_LEGACY, 
            Instant.now().minusSeconds(3600)
        );
        aggregate.execute(seedCmd);
        
        // The violation occurs when we try to update with an effectiveDate OLDER than the seed
        // handled in the When step below
    }

    @When("the UpdateRoutingRuleCmd command is executed with invalid target {string}")
    public void the_update_routing_rule_cmd_command_is_executed_with_invalid_target(String invalidTarget) {
        executeCommand("rule-violation-dual", invalidTarget, Instant.now().plusSeconds(3600));
    }

    @When("the UpdateRoutingRuleCmd command is executed with past effectiveDate")
    public void the_update_routing_rule_cmd_command_is_executed_with_past_effective_date() {
        // Attempting to update with a date in the past (relative to the seed date)
        executeCommand("rule-rollback-test", TARGET_MODERN, Instant.now().minusSeconds(7200));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We check for IllegalArgumentException or IllegalStateException which indicate domain rule violations
        assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, but got: " + caughtException.getClass().getSimpleName()
        );
    }

    // Helper to execute command and capture exceptions
    private void executeCommand(String ruleId, String target, Instant effectiveDate) {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                VALID_ROUTE_ID,
                ruleId,
                target,
                effectiveDate
            );
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
}