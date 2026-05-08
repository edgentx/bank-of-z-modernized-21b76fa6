package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = repository.create("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in When step construction for flexibility, or stored here
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in When step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Defaults for successful scenario
        command = new StartSessionCmd("session-123", "teller-1", "term-1", true, 900, "HOME");
        executeCommand();
    }

    private void executeCommand() {
        try {
            resultEvents = aggregate.execute(command);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("teller-1", event.tellerId());
        Assertions.assertEquals("term-1", event.terminalId());
        Assertions.assertEquals("HOME", event.navigationContext());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = repository.create("session-auth-fail");
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        command = new StartSessionCmd("session-auth-fail", "teller-1", "term-1", false, 900, "HOME");
        executeCommand();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = repository.create("session-timeout-fail");
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void theStartSessionCmdCommandIsExecutedWithInvalidTimeout() {
        command = new StartSessionCmd("session-timeout-fail", "teller-1", "term-1", true, 0, "HOME");
        executeCommand();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigation() {
        aggregate = repository.create("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed with invalid navigation")
    public void theStartSessionCmdCommandIsExecutedWithInvalidNavigation() {
        command = new StartSessionCmd("session-nav-fail", "teller-1", "term-1", true, 900, "");
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // Domain errors manifest as IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
