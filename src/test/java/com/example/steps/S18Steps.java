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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // "Valid" implies ready to start. Not started, authenticated, valid nav state, not timed out.
        aggregate = new TellerSessionAggregate("session-123");
        // Pre-condition: The teller is authenticated.
        // In a real scenario, we might load an aggregate that has an "Authenticated" event applied.
        // For this unit test, we can assume a fresh aggregate is implicitly authenticatable or
        // we simulate the state required for the 'execute' method to succeed.
        // However, based on the pattern, the Command likely contains auth context.
        // The 'valid' aggregate here is simply a new instance that hasn't started yet.
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // We configure the command builder mock or state here
        if (this.command == null) this.command = createDummyCommand();
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        if (this.command == null) this.command = createDummyCommand();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // We need to simulate a state where authentication is missing.
        // Since the command likely carries the token/flag, we will construct
        // a command that explicitly fails the auth check in the execute method.
        // Or, if auth is a state on the aggregate, we would need to set it.
        // Given S-18 description: "Initiates... following successful auth".
        // We will assume the Command has an authenticated flag.
        this.command = new StartSessionCmd("session-auth-fail", "teller-1", "term-1", false, Instant.now().plus(Duration.ofHours(8)));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // The aggregate needs to be in a state where the timeout check fails.
        // Assuming the aggregate tracks 'lastActivityAt'. If we are starting a NEW session,
        // the timeout check usually applies to existing active sessions or the validity of the request.
        // Interpretation: The 'StartSessionCmd' might include a proposed start time, or the aggregate
        // is being reused from a previous idle state.
        // For "StartSession", a violation usually means the request itself is stale or invalid.
        // We will use a command with an EXPIRED timestamp to trigger this failure.
        this.command = new StartSessionCmd("session-timeout-fail", "teller-1", "term-1", true, Instant.now().minus(Duration.ofMinutes(15)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // This implies the Command (which sets the initial navigation state) is invalid.
        // e.g. starting at a screen that doesn't exist or requires previous context.
        this.command = new StartSessionCmd("session-nav-fail", "teller-1", "term-1", true, Instant.now().plus(Duration.ofHours(1)), "INVALID_SCREEN");
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        if (this.command == null) {
            this.command = createDummyCommand();
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
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-1", event.tellerId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // We expect a RuntimeException (IllegalStateException, IllegalArgumentException, etc)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    private StartSessionCmd createDummyCommand() {
        return new StartSessionCmd(
            "session-123",
            "teller-1",
            "term-1",
            true, // authenticated
            Instant.now().plus(Duration.ofHours(8)),
            "HOME" // valid nav state
        );
    }
}
