package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure authenticated for success path
        aggregate.setNavigationState("HOME");
        aggregate.setLastActivity(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Command is created in the When block
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Command is created in the When block
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        command = new StartSessionCmd("session-123", "teller-1", "terminal-A");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        assertEquals("session.started", resultEvents.get(0).type());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // Do NOT mark authenticated. Default is false.
        aggregate.setNavigationState("HOME");
        aggregate.setLastActivity(Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof UnknownCommandException);
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("HOME");
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    }

    // Scenario: StartSessionCmd rejected — Navigation state
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated();
        aggregate.setLastActivity(Instant.now());
        aggregate.setNavigationState("INVALID_STATE");
    }

}
