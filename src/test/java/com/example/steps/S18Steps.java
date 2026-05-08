package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in the 'When' step via the command, defaults to valid
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in the 'When' step via the command, defaults to valid
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.clearAuthentication();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAsTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markNavigationStateInvalid();
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Determine IDs based on context. If 'tellerId' is null (via clearAuthentication), pass null to trigger failure.
        String teller = "teller-1";
        String terminal = "term-1";

        // Check if we are in the violation state for auth (simple check for demo purposes)
        // In a real app, we might pass specific bad data from the Gherkin table or context.
        // Here we rely on the aggregate state modified in the Given steps.
        // However, the command carries the data. To simulate "unauthenticated teller", we pass bad data in command.
        if (aggregate != null) {
             // We construct the command. To trigger the specific validation in `execute`,
             // we rely on the aggregate state check if necessary, but here `StartSessionCmd` carries the IDs.
             // Let's inspect the aggregate state to decide what command to send? No, usually Cucumber sets inputs.
             // The `Given` above modified aggregate state. But `StartSessionCmd` is a record.
             // Let's assume the command is valid, and the aggregate state prevents it (for timeout/nav).
             // For auth, the requirement says "A teller must be authenticated".
             // The `StartSessionCmd` takes a tellerId. If it's null, it fails.
             // We will use a variable to control the command input.
        }
        
        // Check if we are testing the auth failure. Since the aggregate doesn't store "isAuthenticated" for *new* sessions 
        // (it validates the command input), we simulate this by passing null/blank to the command.
        // However, the Given step above `clearAuthentication` suggests internal state.
        // Let's align with the Aggregate implementation: `execute` checks `cmd.tellerId()`.
        // We will pass a null tellerId if the test setup implies it (or just hardcode the scenario logic).
        
        // Scenario 2 logic: Teller not authenticated -> Command has null tellerId
        if (aggregate != null && aggregate.id().equals("session-123")) {
             // Heuristic: We can't easily detect which "Given" ran without storing state.
             // Let's use the exception catching pattern.
        }
        
        // For simplicity in this BDD, we pass a valid command, except where we manually force the scenario.
        // We can inspect the 'aggregate' fields if they were public, or we use a specific context flag.
        // Given the constraints, let's assume the "Given" for Auth sets a flag or we simply send a bad command for that scenario.
        // Actually, the 'Given' `clearAuthentication` sets the aggregate internal state, but the command argument check happens first.
        // I will infer the scenario based on the state of the aggregate in a 'dirty' way or just assume valid command + valid state,
        // except where I explicitly code for the failure.
        
        // Refining approach:
        // S1 (Success): Valid Command, Clean Aggregate.
        // S2 (Auth Fail): Invalid Command (null teller).
        // S3 (Timeout): Valid Command, TimedOut Aggregate.
        // S4 (Nav Fail): Valid Command, Invalid Nav Aggregate.

        String cmdTellerId = "teller-1";
        
        // We can check if the aggregate is in a specific "Violation" state set by the Given methods?
        // The `clearAuthentication` method sets fields to null, but the command is a new object.
        // The validation `if (cmd.tellerId() == null)` is on the COMMAND.
        // So to trigger S2, we MUST pass null in the command.
        // Let's look at the aggregate state. If `clearAuthentication` was called, maybe we want to pass null.
        // (We assume the aggregate instance tells us what context we are in, or we use a shared test context boolean).
        // I will assume if `aggregate` specific state is set, it implies the specific failure scenario.
        
        // NOTE: A cleaner Cucumber would use Examples tables with the IDs. 
        // Since the prompt doesn't use tables, I have to deduce.
        // I will use a flag variable to track which "Given" was called.
        
        if (isAuthViolationScenario) cmdTellerId = null;

        command = new StartSessionCmd("session-123", cmdTellerId, "terminal-1");

        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }
    
    // Helper to detect scenario context (Simplification for single-class step def)
    private boolean isAuthViolationScenario = false;
    
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupAuthViolation() {
        isAuthViolationScenario = true;
        aggregate = new TellerSessionAggregate("session-123");
    }
    
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setupTimeoutViolation() {
        isAuthViolationScenario = false;
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAsTimedOut();
    }
    
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setupNavViolation() {
        isAuthViolationScenario = false;
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markNavigationStateInvalid();
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("terminal-1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check for the specific exception types expected by the invariants
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}