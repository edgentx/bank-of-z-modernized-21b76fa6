package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Defaults to valid state
        this.aggregate = new TellerSessionAggregate(sessionId, true, false, true);
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Valid data will be set in the command constructor
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Valid data will be set in the command constructor
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Construct the command with valid data assuming previous steps
            // If specific invalid state is needed, it's handled in the violated Givens
            String tId = (aggregate != null && aggregate.isActive()) ? "tell-1" : "tell-1";
            String termId = "term-01";
            boolean isAuth = true; // assume true for normal flow
            boolean isTimeout = false;
            String navState = "HOME";

            // Check if we are in a violation scenario (captured in aggregate state)
            // The violation logic is inside the aggregate, driven by its constructor state,
            // not necessarily the command payload itself, though the command carries the context.

            this.command = new StartSessionCmd(tId, termId, isAuth, isTimeout, navState);
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals("tell-1", event.tellerId());
        assertEquals("term-01", event.terminalId());
        assertNotNull(event.occurredAt());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(sessionId, false, false, true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(sessionId, true, true, true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate(sessionId, true, false, false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException for invariant violations
        assertTrue(capturedException instanceof IllegalStateException);
    }
}