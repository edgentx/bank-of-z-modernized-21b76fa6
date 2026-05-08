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
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-2");
        // We construct a command that implies auth, but we can simulate a state where
        // the aggregate knows the user isn't authenticated, or pass a command claiming auth
        // while the aggregate logic determines it's invalid.
        // For BDD, we setup the aggregate to be unauthenticated or pass invalid creds.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-3");
        // To simulate a timeout violation, we might start the session at an old timestamp.
        // However, StartSessionCmd initiates the session. The check is usually against the environment's
        // current time vs. the command time, or a window logic.
        // If the aggregate tracks last activity, this scenario might apply more to ResumeSession.
        // Interpreting strictly: We try to start a session, but the 'request' is effectively stale.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-4");
        // This implies the terminal state (via terminalId or context) is invalid or mismatched.
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Assume valid ID 'TELLER-1'
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Assume valid ID 'TERM-1'
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // We need to determine which Scenario context we are in to pass valid vs invalid data.
        // In Cucumber, this is usually handled by having specific context variables.
        // For simplicity in this Step class, we check the aggregate ID or state.

        String tellerId = "TELLER-1";
        String terminalId = "TERM-1";
        String branchId = "BRANCH-1";
        Instant now = Instant.now();

        // Adjust data based on context defined in Given steps (inspecting aggregate ID or state)
        if (aggregate.id().equals("SESSION-2")) {
            // Violate Authentication: Empty Teller ID or invalid token simulation
            tellerId = "";
        } else if (aggregate.id().equals("SESSION-3")) {
            // Violate Timeout: Simulate a very old timestamp in the command
            now = Instant.now().minusSeconds(3600); // 1 hour ago
        } else if (aggregate.id().equals("SESSION-4")) {
            // Violate Nav State: Empty Terminal ID
            terminalId = "";
        }

        command = new StartSessionCmd(aggregate.id(), tellerId, terminalId, branchId, now);

        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("SESSION-1", event.aggregateId());
        Assertions.assertEquals("TELLER-1", event.tellerId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNull(resultEvents);
        Assertions.assertNotNull(thrownException);
        // Verify it's a domain error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
