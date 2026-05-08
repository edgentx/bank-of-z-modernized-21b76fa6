package com.example.steps;

import com.example.domain.shared.DomainException;
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

/**
 * Cucumber Steps for S-20: EndSessionCmd.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAsAuthenticated("teller-01"); // Sets Active, Authenticated, IDLE, Recent Activity
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The command is created in the When block using the active session details
        // sessionId "session-123" is implicit from the aggregate created above
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            // Create command matching the authenticated state
            command = new EndSessionCmd("session-123", "teller-01");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals("session-123", event.aggregateId());
        assertFalse(aggregate.isActive()); // Verify side effect
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Do not mark as authenticated. isActive is false, tellerId is null.
        // If we need it active but not authenticated (odd state), we manipulate manually:
        // aggregate.markAsAuthenticated(null); // logic depends on implementation
        // Default constructor sets isActive=false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAsAuthenticated("teller-02");
        // Set last activity to 35 minutes ago (Timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(35)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-500");
        aggregate.markAsAuthenticated("teller-03");
        // Set to a state that implies pending transaction or invalid context
        aggregate.setNavigationState("PENDING_TRANSACTION_COMMIT");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In a real app, we might catch a specific DomainException. Here we check for IllegalStateException or similar.
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
