package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in context via command setup
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in context via command setup
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        executeDefaultValidCommand();
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-1", event.terminalId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-fail-auth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-fail-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-fail-nav");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecutedForRejection() {
        String id = aggregate.id();
        
        // Determine which violation to setup based on ID or state
        if (id.contains("fail-auth")) {
            // Authenticated = false
            command = new StartSessionCmd(id, "teller-1", "term-1", false, "HOME", Instant.now());
        } else if (id.contains("fail-timeout")) {
            // Authenticated = true, but activity is old
            Instant oldTime = Instant.now().minus(Duration.ofHours(1));
            command = new StartSessionCmd(id, "teller-1", "term-1", true, "HOME", oldTime);
        } else if (id.contains("fail-nav")) {
            // Authenticated = true, but nav state is blank
            command = new StartSessionCmd(id, "teller-1", "term-1", true, "", Instant.now());
        } else {
            // Should not happen given Given steps
            throw new IllegalStateException("Unknown violation context");
        }

        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected exception but command succeeded");
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException/IllegalArgumentException), got: " + caughtException.getClass().getSimpleName()
        );
    }

    // Helper
    private void executeDefaultValidCommand() {
        command = new StartSessionCmd(aggregate.id(), "teller-1", "term-1", true, "HOME", Instant.now());
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}