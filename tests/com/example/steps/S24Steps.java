package com.example.steps;

import com.example.domain.legacybridge.model.LegacyTransactionRoute;
import com.example.domain.legacybridge.model.RoutingUpdatedEvent;
import com.example.domain.legacybridge.model.UpdateRoutingRuleCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryLegacyTransactionRouteRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for S-24: UpdateRoutingRuleCmd on LegacyTransactionRoute.
 */
public class S24Steps {

    private final InMemoryLegacyTransactionRouteRepository repository = new InMemoryLegacyTransactionRouteRepository();
    private LegacyTransactionRoute aggregate;
    private UpdateRoutingRuleCmd command;
    private Exception caughtException;
    private RoutingUpdatedEvent resultEvent;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRoute("ROUTE-S24");
        repository.save(aggregate);
    }

    @And("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        // ruleId is set when the command is constructed in @When
    }

    @And("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        // newTarget is set when the command is constructed in @When
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        // effectiveDate is set when the command is constructed in @When
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("ROUTE-S24-DUAL");
        aggregate.markDualProcessingViolation();
        repository.save(aggregate);
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("ROUTE-S24-VER");
        aggregate.markVersioningViolation();
        repository.save(aggregate);
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        try {
            command = new UpdateRoutingRuleCmd(
                    aggregate.id(),
                    "MODERN",
                    Instant.parse("2026-06-01T00:00:00Z"),
                    1
            );
            List<DomainEvent> events = aggregate.execute(command);
            if (!events.isEmpty()) {
                resultEvent = (RoutingUpdatedEvent) events.get(0);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNotNull(resultEvent, "Expected routing.updated event to be emitted");
        assertEquals("routing.updated", resultEvent.type());
        assertEquals(aggregate.id(), resultEvent.aggregateId());
        assertEquals("MODERN", resultEvent.newTarget());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException
                || caughtException instanceof IllegalArgumentException);
    }
}
