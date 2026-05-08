package com.example.steps;

import com.example.domain.shared.DomainEvent;
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

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01"); // Ensure authenticated
        aggregate.setCurrentContext("IDLE");       // Ensure valid context
        aggregate.setLastActivityAt(Instant.now()); // Ensure active
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set in the previous step
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "SESSION-TIMEDOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        aggregate.setCurrentContext("IDLE");
        // Set last activity to 31 minutes ago (Timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "SESSION-BUSY";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        aggregate.setCurrentContext("CASH_DEPOSIT"); // Invalid state for termination
        aggregate.setLastActivityAt(Instant.now());
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId, "TELLER-01");
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
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
