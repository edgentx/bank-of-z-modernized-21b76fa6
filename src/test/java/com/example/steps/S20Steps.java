package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.TellerSessionEndedEvent;
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

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123", Duration.ofMinutes(30));
        this.aggregate.markAuthenticated(); // Valid state: authenticated
        this.aggregate.updateLastActivity(Instant.now()); // Valid state: active
        this.aggregate.setNavigationState("MAIN_MENU"); // Valid state: known context
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate constructor in the previous step
        // but we ensure the ID matches if we were passing IDs around explicitly.
        // Here, we just assume the context is set.
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-unauth", Duration.ofMinutes(30));
        // Do NOT mark authenticated. It defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout", Duration.ofMinutes(30));
        this.aggregate.markAuthenticated();
        // Set last activity to 2 hours ago
        this.aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-nav-error", Duration.ofMinutes(30));
        this.aggregate.markAuthenticated();
        this.aggregate.updateLastActivity(Instant.now());
        // Simulate invalid navigation state
        this.aggregate.setNavigationState("UNKNOWN");
    }

    // --- Actions ---

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);
        
        TellerSessionEndedEvent event = (TellerSessionEndedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("session.ended", event.type());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        
        // We verify the message matches the specific invariant violation
        String message = caughtException.getMessage();
        assertTrue(
            message.contains("authenticated") || 
            message.contains("timeout") || 
            message.contains("Navigation state")
        );
    }
}
