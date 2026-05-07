package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Assuming ID generation for the aggregate
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // In this step style, we prepare the command parts. 
        // We will assemble the full command in the 'When' step or store state here.
        // For simplicity in this structure, we'll assume the command is constructed in the When step
        // based on the context established by Givens.
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Same as above.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default successful command construction
        if (command == null) {
            command = new StartSessionCmd(
                    "session-123",
                    "teller-42",
                    "terminal-T7",
                    true, // authenticated
                    Instant.now(),
                    "HOME"
            );
        }
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.getTellerId());
        assertEquals("terminal-T7", event.getTerminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_a_teller_must_be_authenticated_to_initiate_a_session() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        this.command = new StartSessionCmd(
                "session-auth-fail",
                "teller-42",
                "terminal-T7",
                false, // NOT authenticated
                Instant.now(),
                "HOME"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_sessions_must_timeout_after_a_configured_period_of_inactivity() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        // Provide a timestamp that is effectively "old"
        Instant oldTimestamp = Instant.now().minusSeconds(3600); // 1 hour ago
        this.command = new StartSessionCmd(
                "session-timeout",
                "teller-42",
                "terminal-T7",
                true,
                oldTimestamp,
                "HOME"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state_must_accurately_reflect_the_current_operational_context() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        this.command = new StartSessionCmd(
                "session-nav-fail",
                "teller-42",
                "terminal-T7",
                true,
                Instant.now(),
                "INVALID_STATE" // Bad navigation state
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // In Java, domain errors are often modeled as Exceptions (IllegalStateException/IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        System.out.println("Expected error captured: " + capturedException.getMessage());
    }

}
