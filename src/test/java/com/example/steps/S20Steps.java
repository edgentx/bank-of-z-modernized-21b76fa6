package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellerauth.model.*;
import com.example.domain.tellerauth.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Use factory method to create an active, authenticated session
        String id = "session-123";
        String tellerId = "teller-01";
        Instant now = Instant.now();
        Duration timeout = Duration.ofMinutes(30);
        String navState = "HOME";

        this.aggregate = TellerSessionAggregate.create(id, tellerId, now, timeout, navState);
        // Persist to repository (in-memory)
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by using the aggregate loaded/retrieved in previous steps
        Assertions.assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            // Reload from repo to simulate lifecycle
            TellerSessionAggregate agg = repository.findById(aggregate.id()).orElseThrow();
            Command cmd = new EndSessionCmd(agg.id());
            resultEvents = agg.execute(cmd);
            // Save side effects
            agg.clearEvents(); // clear internal list as per lifecycle
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have produced exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create a session but mark it as not authenticated (simulated violation)
        String id = "session-unauth";
        // Bypassing factory validation to set up invalid state for testing purposes
        // Or using a specific 'test-only' constructor/method if domain allows.
        // Here we assume we can construct an object in a bad state to test invariants.
        aggregate = new TellerSessionAggregate(id); 
        // Note: In a real system, we might hydrate from events that led to this state.
        // For this BDD step, we simulate the state directly.
        aggregate.setAuthenticated(false); 
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = "session-timeout";
        Instant longAgo = Instant.now().minus(Duration.ofHours(1));
        Duration timeout = Duration.ofMinutes(30); // 30 min timeout

        // Create a session that was last active 1 hour ago, with a 30 min timeout
        aggregate = new TellerSessionAggregate(id);
        aggregate.setLastActivityTimestamp(longAgo);
        aggregate.setTimeoutDuration(timeout);
        aggregate.setAuthenticated(true); // ensure it is authenticated to isolate timeout check
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        String id = "session-nav-error";
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(true);
        // Set an invalid or null navigation state
        aggregate.setNavigationState(null); 
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check for standard domain exception types or message content
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
