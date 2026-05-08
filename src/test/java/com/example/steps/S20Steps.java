package com.example.steps;

import com.example.domain.navigation.model.EndSessionCmd;
import com.example.domain.navigation.model.TellerSessionAggregate;
import com.example.domain.navigation.model.TellerSessionEndedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.repository.InMemoryTellerSessionRepository;
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
    private Exception caughtException;
    private final String sessionId = "test-session-123";

    // We don't strictly need the repo for aggregate unit logic, but good for wiring context
    // private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Ensure we don't fail timeout check immediately
        aggregate.setLastActivityAt(Instant.now()); 
        aggregate.setCurrentContext("HOME_SCREEN");
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate construction in the previous step
        // If we were passing the ID into the command explicitly, we'd set it up here.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated. Default is false.
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentContext("HOME_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set last activity to 31 minutes ago to violate 30 min timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
        aggregate.setCurrentContext("HOME_SCREEN");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Context is null by default or we can explicitly set it to null
        aggregate.setCurrentContext(null);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            var cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);
        assertEquals("session.ended", resultEvents.get(0).type());
        assertFalse(aggregate.isActive());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect IllegalStateException based on our implementation
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof UnknownCommandException);
    }
}
