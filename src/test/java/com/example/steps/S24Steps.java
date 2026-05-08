package com.example.steps;

import com.example.domain.legacy.model.LegacyTransactionRoute;
import com.example.domain.legacy.model.UpdateRoutingRuleCmd;
import com.example.domain.legacy.model.RoutingUpdatedEvent;
import com.example.domain.shared.Aggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid LegacyTransactionRoute aggregate")
    public void a_valid_LegacyTransactionRoute_aggregate() {
        aggregate = new LegacyTransactionRoute("route-123");
    }

    @Given("a LegacyTransactionRoute aggregate that violates: A transaction must route to exactly one backend system \(modern or legacy\) to prevent dual-processing\.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_dual_processing() {
        aggregate = new LegacyTransactionRoute("route-bad-dual");
        aggregate.markDualProcessingViolation();
    }

    @Given("a LegacyTransactionRoute aggregate that violates: Routing rules must be versioned to allow safe rollback\.")
    public void a_LegacyTransactionRoute_aggregate_that_violates_versioning() {
        aggregate = new LegacyTransactionRoute("route-bad-version");
        // The violation is triggered by the command having invalid version, but we could set internal state here if needed.
        // The acceptance criteria implies the aggregate itself might be in a state, but often this is command-driven.
        // However, to match the "aggregate that violates" phrasing, we can assume we might need a flag on aggregate.
        // But the previous implementation checked ruleVersion on the command.
        // To support this phrasing literally, we might need a flag on aggregate, or interpret it as the command provided is invalid.
        // Let's assume the command will be invalid in the 'When' step.
    }

    @And("a valid ruleId is provided")
    public void a_valid_ruleId_is_provided() {
        // No-op, implied in command creation
    }

    @And("a valid newTarget is provided")
    public void a_valid_newTarget_is_provided() {
        // No-op, implied in command creation
    }

    @And("a valid effectiveDate is provided")
    public void a_valid_effectiveDate_is_provided() {
        // No-op, implied in command creation
    }

    @When("the UpdateRoutingRuleCmd command is executed")
    public void the_UpdateRoutingRuleCmd_command_is_executed() {
        try {
            // Default valid command
            UpdateRoutingRuleCmd cmd = new UpdateRoutingRuleCmd(
                aggregate.id(),
                "rule-1",
                "MODERN",
                Instant.now(),
                1 // Valid version
            );
            
            // If we are in the violation scenario for versioning, we should arguably pass a bad command.
            // The Gherkin for the third scenario says "aggregate that violates", but often this maps to "Given an aggregate AND a command that violates".
            // However, the second scenario has a specific aggregate state helper (markDualProcessingViolation).
            // The third scenario does not have a similar helper in the current aggregate model for "versioning violation state".
            // The logic `if (cmd.newVersion() <= 0)` handles this. 
            // Let's inspect the aggregate state to decide? No, cleaner to just use a valid command here for the happy path
            // and specific commands for the error paths if the Gherkin allowed passing data. 
            // Since Gherkin steps are generic, we rely on the setup. The second scenario setup sets the aggregate.
            // Let's check if we can trigger the version error via the aggregate? The aggregate doesn't have a `markVersioningViolation()`.
            // BUT, the prompt said "Implement... enforce invariants". The logic `if (cmd.newVersion() <= 0)` is command-side validation.
            // The Scenario says: "Given an aggregate that violates... Routing rules must be versioned".
            // This implies the AGGREGATE is broken. But standard DDD puts this check on the command execution.
            // I will assume the standard implementation: `newVersion` check.
            // To make the test fail for the 3rd scenario without changing the signature of the step, 
            // we might need to check if the aggregate ID matches the test scenario context. 
            // That's messy. 
            // Better approach: The Gherkin is slightly ambiguous. I will assume the 'violation' is simulated by the command data, 
            // but since I can't pass data in the step definition easily without a DataTable, 
            // I will check the aggregate ID. If it's the "bad" one, I send a bad command.
            
            if (aggregate.id().equals("route-bad-version")) {
                 cmd = new UpdateRoutingRuleCmd(aggregate.id(), "rule-1", "MODERN", Instant.now(), 0);
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a routing.updated event is emitted")
    public void a_routing_updated_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof RoutingUpdatedEvent);
        
        RoutingUpdatedEvent event = (RoutingUpdatedEvent) resultEvents.get(0);
        assertEquals("routing.updated", event.type());
        assertEquals("route-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for specific error types or messages if necessary
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}
