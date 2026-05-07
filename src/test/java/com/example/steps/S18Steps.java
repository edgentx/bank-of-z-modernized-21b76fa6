package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Parameters stored in context for command creation
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Parameters stored in context for command creation
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Creating a valid command instance for the happy path or other scenarios
        command = new StartSessionCmd("session-1", "teller-123", "term-ABC", Duration.ofHours(8));
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("term-ABC", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // We simulate the violation by not setting up auth context, 
        // which the aggregate logic should detect if we were passing an auth token.
        // Since the command doesn't have an explicit auth token, we assume success unless we add specific checks.
        // For this scenario, we assume the aggregate is in a state where auth is implicitly invalid
        // or the command parameters imply an unauthenticated state.
        // In this simplified model, we assume success unless we explicitly fail.
        // However, to test the rejection, we need to trigger the failure.
        // The prompt says: "violates: A teller must be authenticated".
        // We will rely on the aggregate logic to reject if auth is missing. 
        // Since StartSessionCmd starts a session, maybe the auth is implicit? 
        // Let's assume we pass a null or empty teller ID to simulate a bad command.
        command = new StartSessionCmd("session-2", null, "term-ABC", Duration.ofHours(8));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // Violation: Negative or zero duration
        command = new StartSessionCmd("session-3", "teller-123", "term-ABC", Duration.ZERO);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-4");
        // Violation: Invalid terminal ID (e.g., empty)
        command = new StartSessionCmd("session-4", "teller-123", "", Duration.ofHours(8));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

}
