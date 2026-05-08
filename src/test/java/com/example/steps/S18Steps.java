package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.command.StartSessionCmd;
import com.example.domain.uimodel.event.SessionStartedEvent;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Cucumber Steps for S-18: StartSessionCmd Implementation.
 * <p>
 * Tests the TellerSession aggregate logic using in-memory instances.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private boolean isAuthenticated = true;
    private String tellerId = "TELLER_123";
    private String terminalId = "TERM_01";

    // Helper to create a fresh aggregate
    private void freshAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Reset test state defaults
        this.isAuthenticated = true;
        this.tellerId = "TELLER_123";
        this.terminalId = "TERM_01";
        this.capturedException = null;
        this.resultEvents = null;
    }

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        freshAggregate();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "TELLER_123"; // Valid ID
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "TERM_01"; // Valid Terminal
    }

    // Scenario: StartSessionCmd rejected — A teller must be authenticated
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        freshAggregate();
        this.isAuthenticated = false; // Violation: Not authenticated
    }

    // Scenario: StartSessionCmd rejected — Sessions must timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        freshAggregate();
        // Simulate a session that is already marked as timed out internally
        aggregate.markAsTimedOut();
    }

    // Scenario: StartSessionCmd rejected — Navigation state must accurately reflect
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        freshAggregate();
        // Force state to ACTIVE to simulate a context error (cannot start an already active session)
        aggregate.markNavigationState(TellerSessionAggregate.SessionState.ACTIVE);
    }

    // Execution
    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd("session-123", tellerId, terminalId, isAuthenticated);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Assertions
    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent started = (SessionStartedEvent) event;
        Assertions.assertEquals("session.started", started.type());
        Assertions.assertEquals("TELLER_123", started.tellerId());
        Assertions.assertEquals("TERM_01", started.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it is a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
                capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Expected a domain logic exception, got: " + capturedException.getClass().getSimpleName()
        );
    }
}
