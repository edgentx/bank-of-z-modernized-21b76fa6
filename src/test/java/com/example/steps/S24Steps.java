package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private String ruleId;
    private String newTarget;
    private Instant effectiveDate;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_legacy_transaction_route_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
        caughtException = null;
    }

    @Given("a valid ruleId is provided")
    public void a_valid_rule_id_is_provided() {
        this.ruleId = "rule-ABC";
    }

    @Given("a valid newTarget is provided")
    public void a_valid_new_target_is_provided() {
        this.newTarget = "MODERN";
    }

    @Given("a valid effectiveDate is provided")
    public void a_valid_effective_date_is_provided() {
        this.effectiveDate = Instant.now().plusSeconds(3600);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_update_routing_rule_cmd_command_is_executed() {
        try {
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                    aggregate.id(),
                    ruleId,
                    newTarget,
                    1, // newVersion
                    effectiveDate
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertEquals("RoutingUpdatedEvent", resultEvents.get(0).type());
    }

    // --- Error Scenarios ---

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_legacy_transaction_route_aggregate_that_violates_dual_processing() {
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
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(
                caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Expected a domain error (IllegalStateException or IllegalArgumentException)"
        );
        assertFalse(caughtException.getMessage().isBlank(), "Error message should not be blank");
    }
}
