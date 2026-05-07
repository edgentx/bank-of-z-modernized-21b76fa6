package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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

    // This is a lightweight state holder specific to the test execution context,
    // effectively acting as a transient repository.
    private static class TestContext {
        TellerSessionAggregate aggregate;
        StartSessionCmd command;
        List<DomainEvent> resultEvents;
        Exception caughtException;
    }

    private final TestContext ctx = new TestContext();

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        ctx.aggregate = new TellerSessionAggregate(sessionId);
        // Default valid state: authenticated, ready to start
        ctx.aggregate.markAsAuthenticated();
        ctx.aggregate.setCurrentNavigationState("IDLE");
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // We construct the command in the 'When' step, but we can note the intent here
        // or verify that the command we build will use a valid ID.
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Same as above
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        String sessionId = "session-123";
        String tellerId = "teller-01";
        String terminalId = "term-A";

        // If the specific 'Given' steps haven't defined the command, we default to a valid one.
        // In Cucumber, we often set up state in 'Given' that 'When' consumes.
        ctx.command = new StartSessionCmd(sessionId, tellerId, terminalId);

        try {
            ctx.resultEvents = ctx.aggregate.execute(ctx.command);
        } catch (Exception e) {
            ctx.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(ctx.caughtException, "Should not have thrown an exception");
        assertNotNull(ctx.resultEvents, "Result events list should not be null");
        assertEquals(1, ctx.resultEvents.size(), "Should emit exactly one event");

        DomainEvent event = ctx.resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session.started", started.type());
        assertEquals("session-123", started.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        ctx.aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally NOT calling markAsAuthenticated()
        // Defaults to false, so this violates the invariant.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        ctx.aggregate = new TellerSessionAggregate("session-timeout");
        ctx.aggregate.markAsAuthenticated();
        ctx.aggregate.setCurrentNavigationState("IDLE");

        // Set last activity to 20 minutes ago to violate the 15 minute timeout
        ctx.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        ctx.aggregate = new TellerSessionAggregate("session-nav-error");
        ctx.aggregate.markAsAuthenticated();
        // Set state to something incompatible with starting a new session (e.g. deep in a workflow)
        ctx.aggregate.setCurrentNavigationState("CUSTOMER_SEARCH_IN_PROGRESS");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(ctx.caughtException, "An exception should have been thrown");
        assertTrue(ctx.caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
        
        // Verify the error message matches the invariant logic if desired, or just type checking.
        System.out.println("Caught expected domain error: " + ctx.caughtException.getMessage());
    }

}
