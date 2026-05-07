package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private StartSessionCmd validCmd;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        validCmd = new StartSessionCmd("session-123", "teller-01", "terminal-05");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-404");
        // Simulate a state where auth failed/missing
        aggregate.setAuthenticated(false);
        // The invariant check inside the aggregate logic might rely on external context,
        // but for the aggregate test, we can simulate a rejection by ensuring the state
        // or command triggers the failure.
        // In this implementation, StartSessionCmd assumes valid inputs, but if we extended it
        // to carry a credential token, this would be the place to test invalid token.
        // For now, we test the state 'active' blocking.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Set last activity to 16 minutes ago (timeout is 15m)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1000));
        aggregate.setAuthenticated(true); // Assume auth is ok
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.setActive(true); // Already active, violates "must not start if active"
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in "a valid TellerSession aggregate"
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in "a valid TellerSession aggregate"
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd = (validCmd != null) ? validCmd : new StartSessionCmd("session-123", "teller-01", "terminal-05");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertTrue(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect IllegalStateException for business rule violations
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof UnknownCommandException);
    }
}
