package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Simulate prior initialization to make it valid
        aggregate.setAuthenticatedTeller("TELLER-1");
        aggregate.setLastActivity(Instant.now());
        aggregate.setSessionActive(true);
        aggregate.setInTransaction(false);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Already handled in aggregate constructor or previous step
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        aggregate.setSessionActive(true); // But no teller
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_timed_out() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        aggregate.setAuthenticatedTeller("TELLER-1");
        aggregate.setSessionActive(true);
        // Set last activity to 20 minutes ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_in_transaction() {
        aggregate = new TellerSessionAggregate("SESSION-BUSY");
        aggregate.setAuthenticatedTeller("TELLER-1");
        aggregate.setSessionActive(true);
        aggregate.setLastActivity(Instant.now());
        aggregate.setInTransaction(true); // Violates invariant
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertFalse(aggregate.isSessionActive()); // Verify state change
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown exception");
        // We check for IllegalStateException as the domain error mechanism
        assertTrue(thrownException instanceof IllegalStateException || 
                   thrownException instanceof IllegalArgumentException, 
                   "Expected domain error (IllegalStateException or IllegalArgumentException)");
    }
}
