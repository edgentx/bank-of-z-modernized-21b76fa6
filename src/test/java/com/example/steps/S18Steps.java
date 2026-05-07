package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in the 'When' step via the command object
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in the 'When' step via the command object
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd;
        // We infer the command parameters based on the setup state to satisfy the scenarios
        if (aggregate.id().equals("session-1")) {
            // Valid scenario
            cmd = new StartSessionCmd("session-1", "teller-123", "term-456", true, false, "HOME");
        } else if (aggregate.id().equals("session-2")) {
            // Violation: Auth
            cmd = new StartSessionCmd("session-2", "teller-123", "term-456", false, false, "HOME");
        } else if (aggregate.id().equals("session-3")) {
            // Violation: Timeout
            cmd = new StartSessionCmd("session-3", "teller-123", "term-456", true, true, "HOME");
        } else {
            // Violation: Nav State (session-4)
            cmd = new StartSessionCmd("session-4", "teller-123", "term-456", true, false, "INVALID");
        }

        try {
            resultEvents = aggregate.execute(cmd);
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
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("term-456", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Checking it's a domain logic violation (IllegalStateException based on our implementation)
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
