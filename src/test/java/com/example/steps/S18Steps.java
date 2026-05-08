package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Context for valid/invalid inputs
    private String sessionId = "session-1";
    private String tellerId = "teller-1";
    private String terminalId = "term-1";
    private boolean isAuthenticated = true;
    private boolean isTimedOut = false;
    private boolean isNavValid = true;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Default to valid state
        isAuthenticated = true;
        isTimedOut = false;
        isNavValid = true;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        tellerId = "teller-123";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        terminalId = "terminal-A";
    }

    // NEGATIVE GIVENS

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        isAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        isAuthenticated = true; // Auth is ok
        isTimedOut = true;      // Violating condition
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        isAuthenticated = true; // Auth is ok
        isTimedOut = false;     // Timeout is ok
        isNavValid = false;     // Violating condition
    }

    // WHEN

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, isTimedOut, isNavValid);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // THEN

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception: " + caughtException);
        assertNotNull(resultEvents, "Result events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session.started", started.type());
        assertEquals(sessionId, started.aggregateId());
        assertEquals(tellerId, started.tellerId());
        assertEquals(terminalId, started.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (domain error)");
    }
}
