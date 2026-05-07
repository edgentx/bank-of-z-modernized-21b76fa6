package com.example.steps;

import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in previous step, but ensuring it matches
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        this.sessionId = "session-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Manually force state to unauthenticated to simulate violation for this test
        // In a real scenario, this state would be achieved via previous events or constructor logic
        // For unit test isolation, we might need a package-private setter or a specific factory method
        // Assuming the aggregate starts invalid based on the prompt description of 'violates'
        // We'll use a specific constructor or method if available, otherwise assume standard construction is valid 
        // and we need to invalidate it. 
        // However, standard construction usually implies a valid start. 
        // Let's assume the aggregate handles the check, so we just need a standard one 
        // but we might need to pass null auth details if the constructor allowed it.
        // Based on shared contracts, let's assume we need to set it up such that it fails.
        // For this implementation, we'll assume a standard setup is valid, 
        // and to make it invalid we'd need specific internal access. 
        // We will assume the prompt implies the aggregate logic detects this.
        // Let's rely on the execute method to throw.
        aggregate.markAsUnauthenticated(); // Helper for testing
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsTimedOut(); // Helper for testing
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        this.sessionId = "session-nav-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsNavigationDesync(); // Helper for testing
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertEquals("session.ended", resultEvents.get(0).type(), "Event type should be session.ended");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        // Depending on implementation, this could be IllegalStateException, IllegalArgumentException, or a custom DomainException
        // The prompt says "rejected with a domain error".
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
