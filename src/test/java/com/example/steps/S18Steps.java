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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: TellerSession StartSessionCmd.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private String sessionId = "session-123";
    private String tellerId;
    private String terminalId;
    private boolean isAuthenticated;
    private boolean isTimedOut;
    private String navigationState;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "teller-001";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "term-01";
    }

    // Setup for Negative Scenarios
    
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        a_valid_TellerSession_aggregate();
        a_valid_tellerId_is_provided();
        a_valid_terminalId_is_provided();
        this.isAuthenticated = false; // Violation
        this.isTimedOut = false;
        this.navigationState = "IDLE";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        a_valid_TellerSession_aggregate();
        a_valid_tellerId_is_provided();
        a_valid_terminalId_is_provided();
        this.isAuthenticated = true;
        this.isTimedOut = true; // Violation
        this.navigationState = "IDLE";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        a_valid_TellerSession_aggregate();
        a_valid_tellerId_is_provided();
        a_valid_terminalId_is_provided();
        this.isAuthenticated = true;
        this.isTimedOut = false;
        this.navigationState = "INVALID_CONTEXT"; // Violation
    }

    // Defaults for positive flow
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.") // Reuse context setter
    public void set_defaults_for_positive() {
        // Not strictly needed as we set them individually in positive steps, but ensures cleanliness if order changes.
    }

    // If the standard Given steps are run for the positive scenario, ensure defaults are set
    @Given("a valid TellerSession aggregate") // Context check
    public void ensure_positive_defaults() {
        if (this.navigationState == null) this.navigationState = "IDLE";
        if (this.tellerId == null) this.tellerId = "teller-001";
        if (this.terminalId == null) this.terminalId = "term-01";
        this.isAuthenticated = true;
        this.isTimedOut = false;
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Ensure defaults for positive scenario if not explicitly set negative
            if (navigationState == null) navigationState = "IDLE";

            Command cmd = new StartSessionCmd(
                sessionId,
                tellerId,
                terminalId,
                isAuthenticated,
                isTimedOut,
                navigationState
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain errors are modeled as IllegalStateException or similar RuntimeExceptions in this pattern
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
