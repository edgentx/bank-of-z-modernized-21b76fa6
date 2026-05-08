package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid defaults to ensure success scenario passes
        aggregate.markAuthenticated(true);
        aggregate.setNavigationContextValid(true);
        aggregate.setLastActivityAt(null); // New session
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "sess-violate-auth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(false); // Violation
        aggregate.setNavigationContextValid(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "sess-violate-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.setNavigationContextValid(true);
        // Set last activity to 20 minutes ago (assuming timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "sess-violate-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(true);
        aggregate.setNavigationContextValid(false); // Violation
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "term-T3270-01";
    }

    // --- Whens ---

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid IDs if not explicitly set in scenario (though feature says they are)
        if (tellerId == null) tellerId = "default-teller";
        if (terminalId == null) terminalId = "default-term";

        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no error, but got: " + caughtException);
        Assertions.assertNotNull(resultingEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultingEvents.size(), "Should emit exactly one event");

        DomainEvent event = resultingEvents.get(0);
        Assertions.assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception to be thrown");
        // We check for IllegalStateException which is our standard invariant violation mechanism in this aggregate
        Assertions.assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException, got " + caughtException.getClass().getSimpleName());
    }
}
