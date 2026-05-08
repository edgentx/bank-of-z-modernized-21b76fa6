package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> result;
    private Exception exception;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Field handled in 'When' step construction for simplicity
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Field handled in 'When' step construction for simplicity
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command
        cmd = new StartSessionCmd("session-123", "teller-1", "term-1", true, "HOME");
        try {
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(exception, "Should not have thrown an exception");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(SessionStartedEvent.class, result.get(0).getClass());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted_Unauthenticated() {
        // Create command where isAuthenticated is false
        cmd = new StartSessionCmd("session-auth-fail", "teller-1", "term-1", false, "HOME");
        try {
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(exception);
        assertTrue(exception instanceof IllegalStateException || exception instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markActive(); // Simulate an existing active session which violates the 'fresh start' or timeout logic
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted_TimeoutViolation() {
        cmd = new StartSessionCmd("session-timeout", "teller-1", "term-1", true, "HOME");
        try {
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted_NavigationViolation() {
        // Create command with invalid navigation state
        cmd = new StartSessionCmd("session-nav-fail", "teller-1", "term-1", true, "");
        try {
            result = aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }
}
