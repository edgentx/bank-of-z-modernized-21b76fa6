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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions for Story S-18: StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Given: State Initialization

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("ts-12345");
        this.thrownException = null;
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context setup: Valid IDs are used in the 'When' block.
        // We assume the cmd object created in 'When' handles this, 
        // but we record the intent here.
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context setup
    }

    @Given("the teller is authenticated")
    public void theTellerIsAuthenticated() {
        aggregate.markAsAuthenticated();
    }

    @Given("the teller is NOT authenticated")
    public void theTellerIsNotAuthenticated() {
        // Default state is not authenticated, but explicitly ensuring no flag is set.
        // In the stub, isAuthenticated defaults to false.
    }

    @Given("the session has not timed out")
    public void theSessionHasNotTimedOut() {
        // If we mark authenticated, it sets lastActivity to now.
        if (!aggregate.isAuthenticated) {
            // Just in case we want to test authenticated but old session logic differently later,
            // but for S-18, ensuring activity is recent.
            aggregate.markAsAuthenticated();
        }
    }

    @Given("the session has timed out")
    public void theSessionHasTimedOut() {
        aggregate.markSessionExpired();
    }

    @Given("the navigation state is valid")
    public void theNavigationStateIsValid() {
        aggregate.validateNavigationState();
    }

    @Given("the navigation state is invalid")
    public void theNavigationStateIsInvalid() {
        aggregate.invalidateNavigationState();
    }

    // When: Execution

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("teller-101", "term-202");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    // Then: Assertions

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected a list of events, but got null");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");

        SessionStartedEvent sse = (SessionStartedEvent) event;
        assertEquals("session.started", sse.type());
        assertEquals("ts-12345", sse.aggregateId());
        assertEquals("teller-101", sse.tellerId());
        assertEquals("term-202", sse.terminalId());

        // Verify aggregate version was incremented (via list size check or similar if exposed)
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown, but none was");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify the message matches one of our invariants
        String message = thrownException.getMessage();
        assertTrue(
                message.contains("authenticated") || 
                message.contains("timeout") || 
                message.contains("Navigation state"),
                "Error message did not match expected invariants: " + message
        );
    }
}