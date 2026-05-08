package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-01";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Default valid state setup
        aggregate.markAuthenticated(true);
        aggregate.setOperationalContext(null); // Clean context
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(false); // Not authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.markLastActivityInactive(); // Set last activity to > 15 mins ago
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.setOperationalContext("INVALID_CONTEXT_STATE"); // Set context to something unexpected
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "teller-01";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "term-01";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, "auth-token");
        try {
            aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        List<com.example.domain.shared.DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Should have uncommitted events");
        Assertions.assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        // Check for specific invariant messages
        String message = capturedException.getMessage();
        Assertions.assertTrue(
                message.contains("authenticated") || 
                message.contains("timeout") || 
                message.contains("Navigation state"),
                "Exception message should match invariant violation: " + message
        );
    }
}