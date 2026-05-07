package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String providedSessionId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to create a valid session
    private TellerSessionAggregate createValidSession() {
        TellerSessionAggregate agg = new TellerSessionAggregate("session-123");
        // Simulate the StartSessionCmd execution (not part of this story directly, but needed for setup)
        // We do this by manually setting state for simplicity in the 'Given' setup phase
        // In a real app, we might run a Start command, but the 'Given' is just state setup.
        agg.start("TELLER_01", Instant.now().minusSeconds(60));
        agg.setNavigationState("IDLE", "MAIN_MENU"); // Valid state
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = createValidSession();
        this.providedSessionId = "session-123";
        this.capturedException = null;
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Assuming the command is constructed for the specific aggregate ID
        // The step definition context ensures we use the ID from the aggregate
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-no-auth");
        // Do not start/authenticate the session. IsAuthenticated will return false.
        this.providedSessionId = "session-no-auth";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = createValidSession();
        // Force the last activity time to be way in the past
        this.aggregate.forceLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
        this.providedSessionId = "session-123";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = createValidSession();
        // Set state that implies a transaction is in progress
        this.aggregate.setNavigationState("TXN_ACTIVE", "DEPOSIT_SCREEN");
        this.providedSessionId = "session-123";
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd(providedSessionId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Checking for the specific invariant violation message
        assertTrue(
            capturedException.getMessage().contains("Teller must be authenticated") ||
            capturedException.getMessage().contains("Session has timed out") ||
            capturedException.getMessage().contains("Navigation state must be IDLE")
        );
    }
}
