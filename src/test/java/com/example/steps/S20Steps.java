package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermessaging.model.EndSessionCmd;
import com.example.domain.tellermessaging.model.SessionEndedEvent;
import com.example.domain.tellermessaging.model.TellerSessionAggregate;
import com.example.domain.tellermessaging.model.TellerSessionState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Setup a valid session context
        aggregate = new TellerSessionAggregate("session-1");
        // Simulate a prior event that brought the session to a valid state (e.g., SessionStartedEvent)
        // In a real app, we might load from events, but here we construct state for the test scenario.
        aggregate.__internalSetState(
            TellerSessionState.ACTIVE,
            "teller-123",
            Instant.now().minusSeconds(60), // Last active 60s ago
            "MAIN_MENU"
        );
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate ID in the previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // State is null (not authenticated)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Set last active time to 30 minutes ago (timeout usually 15m)
        aggregate.__internalSetState(
            TellerSessionState.ACTIVE,
            "teller-123",
            Instant.now().minus(Duration.ofMinutes(30)),
            "MAIN_MENU"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Authenticated and Active, but expecting a transaction to be complete
        aggregate.__internalSetState(
            TellerSessionState.ACTIVE,
            "teller-123",
            Instant.now(),
            "AWAITING_TRANSACTION_COMPLETION"
        );
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // We expect IllegalStateException or IllegalArgumentException based on implementation
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }
}
