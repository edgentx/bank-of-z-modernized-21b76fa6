package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    // Standard operational constants
    private static final String VALID_TERMINAL_ID = "TM-400";
    private static final String VALID_TELLER_ID = "U-102";
    private static final String VALID_NAV_STATE = "HOME";
    private static final long CURRENT_TIME = Instant.now().toEpochMilli();

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup usually handled in the 'When' clause or stored in context
        // Here we verify preconditions or just acknowledge the scenario setup.
        assertNotNull(VALID_TELLER_ID);
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        assertNotNull(VALID_TERMINAL_ID);
    }

    // Negative Scenarios Setup

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
    }

    // Action

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // We construct the command based on which scenario we are in.
        // Cucumber contexts are isolated, so we infer the command data from the aggregate ID or specific flags.
        String id = aggregate.id();
        
        // Defaults
        String tId = VALID_TELLER_ID;
        String termId = VALID_TERMINAL_ID;
        boolean auth = true; 
        long time = CURRENT_TIME;
        String nav = VALID_NAV_STATE;

        // Violation overrides based on the aggregate ID set in Given
        if (id.contains("auth-fail")) {
            auth = false;
        } else if (id.contains("timeout")) {
            // Set time to way in the past
            time = Instant.now().minusMillis(1000000).toEpochMilli();
        } else if (id.contains("nav-fail")) {
            nav = "INVALID_STATE";
        }

        Command cmd = new StartSessionCmd(id, tId, termId, auth, time, nav);
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Outcomes

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertEquals("session.started", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

}
