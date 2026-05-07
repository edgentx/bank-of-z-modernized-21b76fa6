package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private String sessionId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "ts-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initSession("teller-101", Instant.now());
        // Ensure valid state
        assertNotNull(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        assertNotNull(sessionId);
        assertEquals(sessionId, aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Session initialized without a teller ID (null) represents auth violation state
        sessionId = "ts-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initSession(null, Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "ts-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initialize with a timestamp that is effectively ancient
        aggregate.initSession("teller-102", Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = "ts-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initSession("teller-103", Instant.now());
        // Simulate a state violation (e.g. setting flag manually)
        aggregate.markNavigationStateInvalid();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        assertEquals("session.ended", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain error exception");
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException,
                   "Exception should be a domain error type");
    }
}
