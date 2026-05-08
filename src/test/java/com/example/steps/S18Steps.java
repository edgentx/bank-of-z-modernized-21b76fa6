package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private String currentTellerId = "teller-1";
    private String currentTerminalId = "term-42";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        this.currentTellerId = "teller-1";
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        this.currentTerminalId = "term-42";
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), currentTellerId, currentTerminalId);
            var events = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Expected events to be emitted");
        assertTrue(events.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-42", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // If the aggregate logic enforces existing authentication state before restarting:
        // This scenario interpretation relies on the aggregate having a state that prevents execution.
        // For this impl, we might simulate this by the aggregate being in a state that rejects Start.
        // However, 'StartSession' usually creates the auth state.
        // We will assume a rule: You cannot start a session if you are already in a 'blocked' or 'unauthenticated' retry state.
        // We'll mark it as unauthenticated to trigger the check inside execute if it existed.
        // Given the current impl allows start if not active, we adjust the aggregate logic in thought:
        // Actually, let's assume the aggregate requires a pre-condition or we mock a failed auth check within the command context.
        // Simpler: The aggregate remembers a previous failed attempt.
        aggregate.markAsUnauthenticated();
        // We need the command execution to fail. I will update the Aggregate logic to check this flag.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Set last activity to ancient times
        aggregate.markAsExpired();
        // And we need to assume we are trying to resume/interact, not start fresh?
        // The prompt says "StartSessionCmd". If it's a new session, inactivity is irrelevant.
        // It implies "ResumeSession" or the aggregate handles timeout.
        // Let's assume the 'StartSession' checks if the ID was previously used and is now timed out.
        // We will rely on the aggregate check.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.corruptNavigationState();
    }
}
