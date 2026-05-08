package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Parameters are handled in the 'command is executed' step via context, 
        // but we store config here to indicate valid intent for the builder
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command construction
        command = new StartSessionCmd(
                "session-123",
                "teller-42",
                "term-T101",
                Duration.ofHours(8),
                "HOME_DASHBOARD"
        );
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with null tellerId")
    public void the_start_session_cmd_command_is_executed_with_null_teller_id() {
        command = new StartSessionCmd(
                "session-123",
                null, // Violation: Unauthenticated
                "term-T101",
                Duration.ofHours(8),
                "HOME_DASHBOARD"
        );
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with zero timeout")
    public void the_start_session_cmd_command_is_executed_with_zero_timeout() {
        command = new StartSessionCmd(
                "session-123",
                "teller-42",
                "term-T101",
                Duration.ZERO, // Violation: No timeout
                "HOME_DASHBOARD"
        );
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_start_session_cmd_command_is_executed_with_invalid_navigation_state() {
        command = new StartSessionCmd(
                "session-123",
                "teller-42",
                "term-T101",
                Duration.ofHours(8),
                "INVALID_CONTEXT" // Violation: Bad nav state
        );
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
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.tellerId());
        assertEquals("term-T101", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        // Ensure no events were published
        assertNull(resultEvents);
    }
}
