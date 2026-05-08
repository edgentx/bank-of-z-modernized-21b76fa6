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
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "TS-12345";
        // Simulate an active session
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Mutating private state via package-private or reflection logic would typically require a factory,
        // but for this test, we assume the aggregate is initialized or hydrated in an 'ACTIVE' state.
        // We will use the specific test constructor or factory available in the aggregate for setup.
        // Assuming the aggregate allows initialization or we use a Test Fixture pattern.
        // For simplicity in this snippet, we rely on the Aggregate having a test-friendly constructor or state.
        // Let's assume we can prime the 'isActive' state via a specific constructor logic for the 'valid' case.
        // In a real scenario, we might apply a 'SessionStartedEvent' first.
        // Here we instantiate with the assumption of validity for the positive case.
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set in the previous step
        Assertions.assertNotNull(this.sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "TS-UNAUTH";
        // Create an aggregate that is not authenticated
        this.aggregate = new TellerSessionAggregate(this.sessionId, false, true, true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "TS-TIMEOUT";
        // Create an aggregate that has timed out
        this.aggregate = new TellerSessionAggregate(this.sessionId, true, false, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "TS-NAV-ERR";
        // Create an aggregate with invalid navigation state
        this.aggregate = new TellerSessionAggregate(this.sessionId, true, true, false);
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            Command cmd = new EndSessionCmd(this.sessionId);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        Assertions.assertNotNull(this.resultEvents);
        Assertions.assertEquals(1, this.resultEvents.size());
        Assertions.assertEquals("session.ended", this.resultEvents.get(0).type());
        Assertions.assertTrue(this.resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(this.capturedException);
        // Checking for IllegalStateException or IllegalArgumentException as per contract
        Assertions.assertTrue(
            this.capturedException instanceof IllegalStateException || 
            this.capturedException instanceof IllegalArgumentException
        );
    }
}