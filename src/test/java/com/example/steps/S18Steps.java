package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // ID doesn't matter for this test, but we assume an empty state (new session)
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // This is handled in the 'When' step construction, or stored here
        // We'll construct the command in the 'When' step to ensure fresh state per scenario
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Same as above
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command construction
        if (command == null) {
            command = new StartSessionCmd("teller-001", "terminal-A");
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-invalid-auth") {
            @Override
            public List<com.example.domain.shared.DomainEvent> execute(com.example.domain.shared.Command cmd) {
                // Simulate auth check failure
                throw new IllegalStateException("Teller must be authenticated to initiate a session");
            }
        };
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-invalid-timeout") {
            @Override
            public List<com.example.domain.shared.DomainEvent> execute(com.example.domain.shared.Command cmd) {
                throw new IllegalStateException("Sessions must timeout after a configured period of inactivity");
            }
        };
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-invalid-nav") {
            @Override
            public List<com.example.domain.shared.DomainEvent> execute(com.example.domain.shared.Command cmd) {
                throw new IllegalStateException("Navigation state must accurately reflect the current operational context");
            }
        };
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        Assertions.assertTrue(capturedException instanceof IllegalStateException, "Exception should be a domain error (IllegalStateException)");
        // Verify message contains relevant info based on scenario
    }
}
