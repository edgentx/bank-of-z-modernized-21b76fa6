package com.example.steps;

import com.example.domain.tellermobile.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String providedTellerId;
    private String providedTerminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.providedTellerId = "teller-001";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.providedTerminalId = "term-42";
    }

    // --- Violations / Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "sess-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Assume valid IDs for the command object, but the aggregate state will block it.
        this.providedTellerId = "teller-001";
        this.providedTerminalId = "term-42";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate an already expired session or invalid state for starting a new one if logic requires.
        // For this scenario, we treat the aggregate as being in a state that prevents start (e.g. already timed out).
        this.providedTellerId = "teller-001";
        this.providedTerminalId = "term-42";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "sess-nav-err";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.providedTellerId = "teller-001";
        this.providedTerminalId = "term-42";
    }

    // --- Execution ---

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(sessionId, providedTellerId, providedTerminalId, Instant.now());
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    // --- Assertions ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
        assertEquals(providedTellerId, event.tellerId());
        assertEquals(providedTerminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
