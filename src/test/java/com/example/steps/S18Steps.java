package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to reset context
    private void resetAggregate(String id) {
        this.aggregate = new TellerSessionAggregate(id);
        this.command = null;
        this.resultEvents = null;
        this.caughtException = null;
    }

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        resetAggregate("ts-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Command is constructed in the 'When' step, this sets context expectation
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid construction
        if (command == null) {
            command = new StartSessionCmd("ts-123", "teller-01", "term-42", true);
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("ts-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-42", event.terminalId());
        assertEquals("session.started", event.type());
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        resetAggregate("ts-auth-fail");
        command = new StartSessionCmd("ts-auth-fail", "teller-01", "term-42", false);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("authenticated"));
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        resetAggregate("ts-timeout-fail");
        // To violate the timeout invariant, we simulate a state where the session is logically expired.
        // The aggregate logic checks if an existing session has timed out.
        // Since we don't have a 'load from history' method exposed here, we verify the logic path.
        // We set the command valid, but the aggregate needs to be in a state that fails.
        // In this specific implementation, the check is `lastActivityAt + timeout < now`.
        // We cannot inject `lastActivityAt` easily without a重构 or using reflection/mutation testing,
        // but the check exists. For this test, we will assume the code is correct or modify the
        // Aggregate to accept a factory that sets time.
        // However, strictly following BDD: We force the condition if possible or rely on the code path.
        // Let's assume we cannot inject the state easily in this step without a snapshot, so we assert the logic exists.
        // Alternatively, we use reflection to set `lastActivityAt`.
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("lastActivityAt");
            field.setAccessible(true);
            // Set activity to 20 minutes ago (Timeout is 15)
            field.set(aggregate, Instant.now().minus(Duration.ofMinutes(20)));
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed", e);
        }
        command = new StartSessionCmd("ts-timeout-fail", "teller-01", "term-42", true);
    }

    // Scenario: StartSessionCmd rejected — Navigation state
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        resetAggregate("ts-nav-fail");
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("terminalId");
            field.setAccessible(true);
            // Set existing terminal to something else
            field.set(aggregate, "term-99");
        } catch (Exception e) {
            throw new RuntimeException("Test setup failed", e);
        }
        // Command tries to start session on a different terminal (term-42) while aggregate thinks it is on term-99
        command = new StartSessionCmd("ts-nav-fail", "teller-01", "term-42", true);
    }

}
