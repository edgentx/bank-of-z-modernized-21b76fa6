package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Create a valid session: authenticated, recent activity, valid state
        aggregate = new TellerSessionAggregate("session-1", true, Instant.now(), "HOME");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate creation in the previous step
        // or could be used to construct the command explicitly if needed.
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("session-1", event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Not authenticated
        aggregate = new TellerSessionAggregate("session-2", false, Instant.now(), "HOME");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Last activity was 1 hour ago (exceeds typical 30 min timeout)
        aggregate = new TellerSessionAggregate("session-3", true, Instant.now().minusSeconds(3600), "HOME");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // Invalid navigation state
        aggregate = new TellerSessionAggregate("session-4", true, Instant.now(), "");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Typically we might check for a specific DomainException type, but here we check the message
        // as we are throwing IllegalStateException from the aggregate.
        assertTrue(capturedException.getMessage().contains("Teller must be authenticated") ||
                   capturedException.getMessage().contains("timed out") ||
                   capturedException.getMessage().contains("Navigation state"));
    }
}
