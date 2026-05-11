package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationState("HOME");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        aggregate.setAuthenticated(false); // Violation
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationState("HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago to violate 15 minute timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.setNavigationState("HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(15));
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationState(""); // Violation: Empty/Invalid state
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The aggregate is already initialized with a valid ID in the Given steps
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");

        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        // We check for IllegalStateException or similar domain logic exceptions
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof UnknownCommandException,
            "Exception should be a domain error (IllegalStateException)");
    }
}
