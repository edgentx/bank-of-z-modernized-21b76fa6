package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate session;
    private Throwable thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Setup a healthy session with valid auth, active state, and valid nav context
        session = new TellerSessionAggregate("SESSION-123");
        // Initialize state by simulating past events (in a real repo we'd load from history)
        // Here we assume a constructor that allows us to rebuild state or a public test setup method.
        // Since the aggregate provided initializes empty, we simulate the prior state needed for 'Valid'
        // We use reflection or package-private helper if needed, but here we assume the 'Valid' context
        // implies the aggregate is ready to accept commands.
        
        // Note: In TDD, we'd often hydrate from past events. For this step def, we simulate the state:
        simulateValidActiveSession(session);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        session = new TellerSessionAggregate("SESSION-999");
        // Ensure authenticatedUser is null or invalid
        // The constructor defaults fields to null/empty, which represents 'not authenticated'.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        session = new TellerSessionAggregate("SESSION-TIMEOUT");
        simulateValidActiveSession(session);
        // Force lastActivityTimestamp to be ancient (simulating timeout)
        session.setLastActivityTime(Instant.now().minus(Duration.ofMinutes(30))); // Assuming 15 min timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        session = new TellerSessionAggregate("SESSION-BAD-NAV");
        simulateValidActiveSession(session);
        // Corrupt the navigation state or put it in a state where EndSession is invalid (e.g., Deep Transaction)
        session.setCurrentScreen("DEEP_TRANSACTION_COMMIT"); // Assume EndSession requires main menu
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by creating the aggregate with ID in previous steps
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(session.id(), "USER_INITIATED");
            resultEvents = session.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Should have thrown an exception");
        // Depending on implementation, it might be IllegalStateException, IllegalArgumentException, or a custom DomainException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof DomainException);
    }

    // Helper to simulate a valid hydrated state for the aggregate
    private void simulateValidActiveSession(TellerSessionAggregate agg) {
        // We effectively bypass the event sourcing hydration here for unit test speed,
        // setting the internal state that would exist if a session started successfully.
        agg.setAuthenticatedTeller("TELLER_001");
        agg.setLastActivityTime(Instant.now());
        agg.setCurrentScreen("MAIN_MENU");
    }
}
