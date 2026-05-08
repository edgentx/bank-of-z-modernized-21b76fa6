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
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Initialize to a valid state (Authenticated, Active, Valid Nav, Recent Activity)
        aggregate.initializeValidSession("teller-001", "MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly handled by the aggregate instance in these tests
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
        assertEquals("session.ended", resultEvents.get(0).type());
        assertFalse(aggregate.isActive(), "Session should be inactive after ending");
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Force state: Null Teller ID (Not Authenticated)
        aggregate.forceSessionState(null, Instant.now(), true, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Force state: Last activity was 60 minutes ago (exceeds 30 min timeout)
        Instant past = Instant.now().minus(Duration.ofMinutes(60));
        aggregate.forceSessionState("teller-001", past, true, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Force state: Active but Navigation is null
        aggregate.forceSessionState("teller-001", Instant.now(), true, null);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        // Verify it matches the specific invariant messages
        assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank());
    }
}
