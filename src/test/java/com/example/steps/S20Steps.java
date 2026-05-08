package com.example.steps;

import com.example.domain.aggregator.model.*;
import com.example.domain.aggregator.repository.TellerSessionRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a previously initiated session
        aggregate.loadFromHistory(List.of(
            new SessionInitiatedEvent(sessionId, "teller-1", Instant.now())
        ));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicit in the aggregate initialization, ensures state is valid
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Aggregate exists but no InitiatedEvent (auth) has occurred
        String sessionId = "session-unauthenticated";
        aggregate = new TellerSessionAggregate(sessionId);
        // No events applied -> session not active/authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-timedout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a session initiated 5 hours ago (assuming 30min timeout)
        Instant past = Instant.now().minus(Duration.ofHours(5));
        aggregate.loadFromHistory(List.of(
            new SessionInitiatedEvent(sessionId, "teller-1", past)
        ));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.loadFromHistory(List.of(
            new SessionInitiatedEvent(sessionId, "teller-1", Instant.now())
        ));
        // Force the aggregate into an invalid navigation state (e.g., deep transaction)
        // This is simulated by a test hook or specific invalid state setter if available.
        // For this test, we assume the aggregate logic checks for deep navigation.
        // We'll inject a flag or use a specific test constructor if complex.
        // Here we rely on the aggregate's internal check against 'invalid context'.
        aggregate.markNavigationStateInvalidForTest(); 
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected exception but command succeeded");
        // Depending on implementation, could be IllegalStateException or specific DomainException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}