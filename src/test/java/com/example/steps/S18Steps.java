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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context implies we use valid data in the command execution
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context implies we use valid data in the command execution
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        executeCommand("teller-123", "term-456", true);
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

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("session-nav-error");
    }

    // We use a helper to differentiate the valid from invalid scenarios based on context state
    private void executeCommand(String tellerId, String terminalId, boolean expectValid) {
        try {
            // For invalid scenarios, we rely on the aggregate's internal state or pre-conditions
            // to throw. Since TellerSession is new, we might simulate the violation by passing
            // specific data or checking the aggregate state. 
            // Assuming for S-18 we enforce basic existence checks.
            StartSessionCmd cmd = new StartSessionCmd("session-1", tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    // When step for negative scenarios
    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_invalid() {
        try {
            // Attempt to execute. If aggregate logic throws, we catch it.
            // We use invalid IDs or setup depending on the specific 'violation' context
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), "invalid", "invalid");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception but command succeeded");
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
        assertNull(resultEvents, "No events should be emitted when command is rejected");
    }

    @Then("the session is marked as active")
    public void the_session_is_marked_as_active() {
        assertTrue(aggregate.isActive(), "Session should be active after successful start");
    }
}
