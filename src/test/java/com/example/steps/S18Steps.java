package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Step definitions for S-18: Implement StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Ensure preconditions for success are met
        aggregate.markAuthenticated();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Setup handled in execute step, or we could store state here
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Setup handled in execute step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            String tellerId = "TELLER-001";
            String terminalId = "TERM-101";
            command = new StartSessionCmd(tellerId, terminalId);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Deliberately NOT calling markAuthenticated() to violate the invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(); // Valid auth
        aggregate.markStale(); // Violates timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String id = java.util.UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(); // Valid auth
        aggregate.invalidateContext(); // Violates nav state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Exception should have been thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Placeholder for step concatenation if needed
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Placeholder for step concatenation if needed
    }
}
