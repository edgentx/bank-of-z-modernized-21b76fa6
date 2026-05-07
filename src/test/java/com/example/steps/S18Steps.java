package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    // Test Context
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    
    // Flags to simulate Gherkin constraints
    private boolean isAuthenticated = true;
    private boolean isTimeoutViolated = false;
    private boolean isNavigationStateValid = true;

    // Execution Result
    private List<com.example.domain.shared.DomainEvent> events;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "term-A";
    }

    // --- Constraint Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        this.isAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        this.isTimeoutViolated = true;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        this.isNavigationStateValid = false;
    }

    // --- Actions ---

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                    this.sessionId,
                    this.tellerId,
                    this.terminalId,
                    this.isAuthenticated,
                    this.isTimeoutViolated,
                    this.isNavigationStateValid
            );
            this.events = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(events, "Events list should not be null");
        assertFalse(events.isEmpty(), "Events list should not be empty");
        
        com.example.domain.shared.DomainEvent event = events.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session.started", started.type());
        assertEquals(this.tellerId, started.tellerId());
        assertEquals(this.terminalId, started.terminalId());
        assertEquals(this.sessionId, started.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(
                caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
                "Expected domain error (IllegalArgumentException or IllegalStateException), got: " + caughtException.getClass().getSimpleName()
        );
    }
}