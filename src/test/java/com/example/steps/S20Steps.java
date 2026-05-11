package com.example.steps;

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
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with a valid state: authenticated, active, neutral context
        aggregate.hydrate(
            "teller-42", 
            true, 
            Instant.now(), 
            "DASHBOARD", 
            false
        );
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the setup in 'a valid TellerSession aggregate'
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
        // Invariant check: state changed
        assertTrue(aggregate.isEnded());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with unauthenticated state
        aggregate.hydrate(
            null, 
            false, 
            Instant.now(), 
            "LOGIN_SCREEN", 
            false
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with a session that was active long ago (> 15 mins)
        aggregate.hydrate(
            "teller-42", 
            true, 
            Instant.now().minus(Duration.ofMinutes(20)), 
            "DASHBOARD", 
            false
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-busy";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate with a state that blocks closing (e.g. deep in a transaction)
        aggregate.hydrate(
            "teller-42", 
            true, 
            Instant.now(), 
            "TRANSACTION_IN_PROGRESS", 
            false
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect an IllegalStateException wrapping the domain rule violation
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("Cannot end session"));
    }
}
