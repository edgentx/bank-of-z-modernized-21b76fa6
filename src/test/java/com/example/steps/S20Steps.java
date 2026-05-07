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
    private String sessionId;

    // Helper to create a generic valid aggregate for the "Happy Path" and setup
    private TellerSessionAggregate createValidAggregate() {
        String id = "session-" + System.currentTimeMillis();
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        agg.setActive(true);
        agg.setAuthenticated(true); // Must be authenticated
        agg.setLastActivityAt(Instant.now()); // Activity is recent
        agg.setCurrentNavigationState("IDLE"); // Valid context
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // In this pattern, the sessionId is implicitly part of the aggregate instance
        // or the command. Here we assume the command targets the loaded aggregate's ID.
        sessionId = aggregate.id();
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event must be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = createValidAggregate();
        aggregate.setAuthenticated(false); // Violate: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = createValidAggregate();
        // Set last activity to 16 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(16)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = createValidAggregate();
        // Use the specific state key that triggers the invariant logic
        aggregate.setCurrentNavigationState("INVALID_CONTEXT");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify no events were produced
        assertNull(resultEvents, "No events should be produced on failure");
        // Or check aggregate uncommitted events are empty
        assertTrue(aggregate.uncommittedEvents().isEmpty());
    }
}
