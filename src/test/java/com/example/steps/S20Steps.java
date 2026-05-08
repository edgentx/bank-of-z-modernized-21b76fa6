package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private String sessionId = "session-123";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate to a valid state (Authenticated, Active, Valid Location)
        aggregate.hydrateForTesting(
            "teller-100", 
            true, 
            Instant.now(), 
            "MAIN_MENU"
        );
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled in the setup, but we assert existence
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            // Reload aggregate from repo to simulate clean fetch
            var loadedAggregate = repository.findById(sessionId).orElseThrow();
            Command cmd = new EndSessionCmd(sessionId);
            resultEvents = loadedAggregate.execute(cmd);
            // Save changes
            repository.save(loadedAggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
        
        // Verify state change
        var savedAggregate = repository.findById(sessionId).orElseThrow();
        assertFalse(savedAggregate.isActive());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // isAuthenticated = false
        aggregate.hydrateForTesting(
            "teller-100", 
            false, // Violates invariant
            Instant.now(), 
            "MAIN_MENU"
        );
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // lastActivityAt > 30 mins ago
        aggregate.hydrateForTesting(
            "teller-100", 
            true, 
            Instant.now().minus(Duration.ofMinutes(31)), // Violates invariant
            "MAIN_MENU"
        );
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Location is null or blank
        aggregate.hydrateForTesting(
            "teller-100", 
            true, 
            Instant.now(), 
            null // Violates invariant
        );
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("must") || 
                   capturedException.getMessage().contains("timeout") ||
                   capturedException.getMessage().contains("Navigation"));
        
        // Verify no event was emitted
        assertNull(resultEvents);
        
        // Verify state did not change (session still active)
        var savedAggregate = repository.findById(sessionId).orElseThrow();
        assertTrue(savedAggregate.isActive());
    }
}
