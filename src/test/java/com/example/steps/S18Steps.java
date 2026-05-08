package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    @Autowired
    private TellerSessionRepository repository;

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = repository.create("session-123");
        aggregate.markAuthenticated(); // Ensure authenticated for success case
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Implicit in command construction in 'When'
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Implicit in command construction in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        Command cmd = new StartSessionCmd("session-123", "teller-1", "terminal-A");
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
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("terminal-A", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = repository.create("session-401");
        // Defaults to !authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = repository.create("session-timeout");
        aggregate.markAuthenticated();
        // Set activity to 16 minutes ago
        aggregate.setLastActivityAt(Instant.now().minusSeconds(960));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = repository.create("session-nav-error");
        aggregate.markAuthenticated();
        // Assuming we can force state or re-use aggregate that is already started
        // For this aggregate, valid start is from NONE. If we set it to STARTED manually via a hypothetical setter or reused it:
        // We simulate this by re-using an aggregate that has already processed a command.
        aggregate.execute(new StartSessionCmd("session-nav-error", "teller-1", "term-1"));
        // Now it's in STARTED state, so next command violates invariant.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
