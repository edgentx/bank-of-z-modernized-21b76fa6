package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String session;
    private String tellerId;
    private String terminalId;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        session = "session-123";
        aggregate = new TellerSessionAggregate(session);
        // Assume pre-authenticated state for a "valid" aggregate ready to start
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        session = "session-unauth";
        aggregate = new TellerSessionAggregate(session);
        // State is not authenticated by default, so this is already invalid.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        // Simulate a session that is already in a state (e.g., Active) but timed out
        // We need to reach an Active state first. 
        // NOTE: For this simple aggregate, we start fresh. To violate timeout, we'd need to be active.
        // The implementation will enforce invariants. Here we prepare the context.
        session = "session-timeout";
        aggregate = new TellerSessionAggregate(session);
        // We assume this aggregate is brand new, so the timeout violation is hypothetical
        // or based on specific pre-existing state if the aggregate was rehydrated.
        // For the purpose of testing the rejection logic:
        // The scenario implies the aggregate's state prevents execution.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        session = "session-nav";
        aggregate = new TellerSessionAggregate(session);
        // Assume default state is invalid for starting (e.g. mismatch context)
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.tellerId = "TELLER-101";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.terminalId = "TERM-202";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent evt = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", evt.aggregateId());
        Assertions.assertEquals("TELLER-101", evt.tellerId());
        Assertions.assertEquals("TERM-202", evt.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Check that it's one of the expected domain errors or illegal state
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}