package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1 & Helpers
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Stored via command builder in next step or context
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Stored via command builder in next step or context
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command context if not overridden in specific scenario logic
        if (command == null) {
            command = new StartSessionCmd("teller-01", "term-05", "MAIN_MENU", true);
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException.getMessage());
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("session-123", event.aggregateId());
    }

    // Scenario 2: Authentication
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        command = new StartSessionCmd("teller-01", "term-05", "MAIN_MENU", false); // isAuthenticated = false
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception");
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }

    // Scenario 3: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAsTimedOut(); // Helper to simulate state
        command = new StartSessionCmd("teller-01", "term-05", "MAIN_MENU", true);
    }

    // Scenario 4: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        command = new StartSessionCmd("teller-01", "term-05", null, true); // Invalid nav state
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainErrorGeneric() {
        // Reusing the validation for Scenarios 3 & 4
        Assertions.assertNotNull(caughtException, "Expected a domain error exception");
        // Scenario 3 is IllegalStateException, Scenario 4 is IllegalArgumentException
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException
        );
    }
}