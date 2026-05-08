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
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate to a valid "Active" state for a positive test
        aggregate.hydrate(true, Instant.now(), true);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the previous step setup
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(sessionId, "TELLER-1");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "TS-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Set authenticated to false
        aggregate.hydrate(false, Instant.now(), true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "TS-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        // Set last activity to 20 minutes ago (timeout is 15)
        aggregate.hydrate(true, Instant.now().minus(Duration.ofMinutes(20)), true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "TS-NAV-ERR";
        aggregate = new TellerSessionAggregate(sessionId);
        // Set inNavigationContext to false (e.g. lost or in invalid screen)
        aggregate.hydrate(true, Instant.now(), false);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain exception, but command succeeded.");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
