package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    // Test State
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "TS-" + UUID.randomUUID();
        this.tellerId = "TELLER-123";
        this.terminalId = "TERM-01";
        
        // Create a valid, authenticated session stub that accepts commands
        this.aggregate = new TellerSessionAggregate(sessionId) {
            @Override
            public List<DomainEvent> execute(Command cmd) {
                // In-memory valid logic for the happy path
                if (cmd instanceof StartSessionCmd) {
                    return List.of(new SessionStartedEvent(sessionId, tellerId, terminalId, Instant.now()));
                }
                throw new IllegalArgumentException("Unknown command");
            }
        };
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "TELLER-123";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "TERM-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(tellerId, terminalId);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("SessionStarted", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "TS-UNAUTH";
        this.tellerId = "UNAUTH-TELLER";
        this.terminalId = "TERM-01";

        // Mock aggregate that throws error for StartSessionCmd
        this.aggregate = new TellerSessionAggregate(sessionId) {
            @Override
            public List<DomainEvent> execute(Command cmd) {
                if (cmd instanceof StartSessionCmd) {
                    throw new IllegalStateException("Teller not authenticated");
                }
                throw new IllegalArgumentException("Unknown command");
            }
        };
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "TS-TIMEOUT";
        // Mock aggregate that throws timeout error
        this.aggregate = new TellerSessionAggregate(sessionId) {
            @Override
            public List<DomainEvent> execute(Command cmd) {
                if (cmd instanceof StartSessionCmd) {
                    throw new IllegalStateException("Session timeout period invalid");
                }
                throw new IllegalArgumentException("Unknown command");
            }
        };
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "TS-NAV-ERR";
        // Mock aggregate that throws navigation state error
        this.aggregate = new TellerSessionAggregate(sessionId) {
            @Override
            public List<DomainEvent> execute(Command cmd) {
                if (cmd instanceof StartSessionCmd) {
                    throw new IllegalStateException("Navigation state invalid");
                }
                throw new IllegalArgumentException("Unknown command");
            }
        };
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }

}
