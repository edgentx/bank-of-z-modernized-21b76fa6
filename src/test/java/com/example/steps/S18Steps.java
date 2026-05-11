package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
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
        this.aggregate = new TellerSessionAggregate("session-123");
        // By default, for a "valid" aggregate scenario, we assume the teller is authenticated
        // and state is valid unless specified otherwise by other steps.
        this.aggregate.markAuthenticated();
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in the 'when' step construction
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in the 'when' step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Executing the command with standard valid data
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-42", "terminal-T1");
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.tellerId());
        assertEquals("terminal-T1", event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // The aggregate starts as unauthenticated by default.
        // We ensure we do NOT call markAuthenticated().
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        this.aggregate.markAuthenticated(); // Ensure auth passes
        this.aggregate.markSessionExpired(); // Force expired state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        this.aggregate.markAuthenticated(); // Ensure auth passes
        this.aggregate.markNavigationInvalid(); // Force invalid nav state
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In this domain layer, errors are modeled via Exceptions (IllegalStateException/IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Exception should be a domain logic exception");
    }

    // Helper to execute and capture results
    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}