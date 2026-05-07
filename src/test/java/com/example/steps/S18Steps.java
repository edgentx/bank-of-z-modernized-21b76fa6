package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
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

    // Test Context
    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Setup Helpers
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_01";
    private static final String VALID_SESSION_ID = "SESS_01";
    private static final String VALID_NAV_STATE = "MAIN_MENU";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in context builder
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in context builder
    }

    // --- Scenarios ---

    // Scenario 1: Success
    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Default to valid command if not overridden by specific Given violations
        if (this.command == null) {
            this.command = new StartSessionCmd(
                VALID_SESSION_ID,
                VALID_TELLER_ID,
                VALID_TERMINAL_ID,
                true, // authenticated
                Instant.now(),
                VALID_NAV_STATE
            );
        }
        
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "Events should not be empty");
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(VALID_SESSION_ID, startedEvent.aggregateId());
    }

    // Scenario 2: Not Authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        this.aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Auth is false
        this.command = new StartSessionCmd(
            VALID_SESSION_ID,
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            false, // NOT authenticated
            Instant.now(),
            VALID_NAV_STATE
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected domain error (IAE)");
    }

    // Scenario 3: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Create a command with a timestamp way in the past to simulate timeout/stale request
        Instant past = Instant.now().minusSeconds(3600); // 1 hour ago
        this.command = new StartSessionCmd(
            VALID_SESSION_ID,
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            true,
            past, // Old timestamp
            VALID_NAV_STATE
        );
    }

    // Scenario 4: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Blank or null nav state
        this.command = new StartSessionCmd(
            VALID_SESSION_ID,
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            true,
            Instant.now(),
            "" // Invalid nav state
        );
    }
}
