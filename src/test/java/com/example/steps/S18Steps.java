package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup failure: isAuthenticated = false
        cmd = new StartSessionCmd("teller-1", "term-1", false, false, true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup failure: isTimedOut = true
        cmd = new StartSessionCmd("teller-1", "term-1", true, true, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup failure: isNavigationStateValid = false
        cmd = new StartSessionCmd("teller-1", "term-1", true, false, false);
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Command is constructed in the 'When' step with valid data for the happy path
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Command is constructed in the 'When' step with valid data for the happy path
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // If command wasn't pre-set by a violation scenario, build a valid one for the happy path
            if (cmd == null) {
                cmd = new StartSessionCmd("teller-1", "term-1", true, false, true);
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
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
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Invariants are enforced via IllegalStateException within the aggregate
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage() != null && !caughtException.getMessage().isBlank());
    }
}
