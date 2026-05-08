package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private StartSessionCmd cmd;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated(); // Ensure it is valid by default
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in When clause construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in When clause construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            cmd = new StartSessionCmd("session-1", "teller-123", "term-456");
            var events = aggregate.execute(cmd);
            // Verify event emission immediately for the test context
            assertFalse(events.isEmpty());
            assertTrue(events.get(0) instanceof SessionStartedEvent);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof SessionStartedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-fail-auth");
        // Intentionally NOT marking as authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-fail-timeout");
        aggregate.markAuthenticated(); // Valid auth
        aggregate.markTimedOut(); // Invalid state
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-fail-nav");
        aggregate.markAuthenticated(); // Valid auth
        aggregate.markContextInvalid(); // Invalid state
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
