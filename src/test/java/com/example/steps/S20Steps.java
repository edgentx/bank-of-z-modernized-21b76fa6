package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20: TellerSession EndSessionCmd.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "SESSION-" + System.currentTimeMillis();
        this.aggregate = new TellerSessionAggregate(sessionId);
        
        // Setup a valid, active state
        aggregate.setActive(true);
        aggregate.markAuthenticated(true);
        aggregate.setCurrentScreen("HOME"); // Valid state for termination
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The sessionId is already set in the aggregate constructor
        assertNotNull(sessionId);
    }

    // --- Scenario: EndSessionCmd rejected — A teller must be authenticated ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "SESSION-AUTH-FAIL";
        this.aggregate = new TellerSessionAggregate(sessionId);
        
        // Force valid state EXCEPT authentication
        aggregate.setActive(true);
        aggregate.markAuthenticated(false); // Violation: Not authenticated
        aggregate.setCurrentScreen("HOME");
        aggregate.setLastActivityAt(Instant.now());
    }

    // --- Scenario: EndSessionCmd rejected — Sessions must timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "SESSION-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);

        // Force valid state EXCEPT activity time
        aggregate.setActive(true);
        aggregate.markAuthenticated(true);
        aggregate.setCurrentScreen("HOME");
        // Set last activity to 20 minutes ago (Violation: Timeout is 15 mins)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    // --- Scenario: EndSessionCmd rejected — Navigation state must accurately reflect ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "SESSION-NAV-ERR";
        this.aggregate = new TellerSessionAggregate(sessionId);

        // Force valid state EXCEPT navigation
        aggregate.setActive(true);
        aggregate.markAuthenticated(true);
        // Violation: Not in HOME/IDLE, stuck in a transaction screen
        aggregate.setCurrentScreen("CASH_DEPOSIT_ENTRY");
        aggregate.setLastActivityAt(Instant.now());
    }

    // --- Action ---

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event must be SessionEndedEvent");
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals(sessionId, endedEvent.sessionId());
        
        // Verify aggregate state changes
        assertFalse(aggregate.isActive(), "Session should be inactive");
        assertFalse(aggregate.isAuthenticated(), "Teller should be logged out");
        assertNull(aggregate.getCurrentScreen(), "Navigation state should be cleared");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify no events were emitted
        assertTrue(
            resultEvents == null || resultEvents.isEmpty(), 
            "No events should be emitted when command is rejected"
        );
    }
}
