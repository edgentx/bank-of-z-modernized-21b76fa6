package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermaintenance.model.StartSessionCmd;
import com.example.domain.tellermaintenance.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private StartSessionCmd cmd;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // Simulate authenticated, active, valid context state via constructor or factory not exposed in this snippet, 
        // but assuming aggregate starts valid or we hydrate it.
        // For S-18 validity, we assume defaults are valid unless violated.
        aggregate.setAuthenticated(true);
        aggregate.setTimeoutInstant(Instant.now().plusSeconds(3600)); // 1 hour from now
        aggregate.setNavigationStateValid(true);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Prepared in the @When step via the command
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Prepared in the @When step via the command
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        cmd = new StartSessionCmd("session-1", "teller-123", "term-456", Instant.now().plusSeconds(1800));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("session.started", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-2");
        aggregate.setAuthenticated(false); // Violation
        aggregate.setTimeoutInstant(Instant.now().plusSeconds(3600));
        aggregate.setNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.setAuthenticated(true);
        aggregate.setTimeoutInstant(Instant.now().minusSeconds(10)); // Expired
        aggregate.setNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.setAuthenticated(true);
        aggregate.setTimeoutInstant(Instant.now().plusSeconds(3600));
        aggregate.setNavigationStateValid(false); // Violation
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Domain errors are modeled as RuntimeExceptions (IllegalStateException, IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank());
    }
}