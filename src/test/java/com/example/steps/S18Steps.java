package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Pre-condition for valid start usually
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in When construction, but we can track valid state here if needed
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in When construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeCommand("TELLER-01", "TERM-05", "CONTEXT_HOME");
    }

    private void executeCommand(String tId, String termId, String ctx) {
        try {
            cmd = new StartSessionCmd(aggregate.id(), tId, termId, ctx);
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted, but got exception: " + 
            (capturedException != null ? capturedException.getMessage() : "none"));
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Do NOT call markAuthenticated(). isAuthenticated defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(); 
        aggregate.markStale(); // Set last activity to > 15 mins ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        // We will pass a context string indicating invalid state in the When step
    }

    // --- Specific When for Violations --

    @When("the StartSessionCmd command is executed with invalid auth context")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        executeCommand("TELLER_UNAUTH", "TERM-01", "HOME");
    }

    @When("the StartSessionCmd command is executed with stale context")
    public void theStartSessionCmdCommandIsExecutedWithStaleContext() {
        executeCommand("TELLER_01", "TERM-02", "HOME");
    }

    @When("the StartSessionCmd command is executed with invalid nav context")
    public void theStartSessionCmdCommandIsExecutedWithInvalidNavContext() {
        executeCommand("TELLER_01", "TERM-03", "CONTEXT_INVALID_STATE");
    }

    // --- Generic Then for Domain Error --

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception but command succeeded.");
        // We typically check for IllegalStateException or DomainException
        Assertions.assertTrue(capturedException instanceof IllegalStateException 
            || capturedException instanceof IllegalArgumentException);
    }
}