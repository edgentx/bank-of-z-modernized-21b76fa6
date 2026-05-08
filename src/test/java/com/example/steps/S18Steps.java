package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private StartSessionCmd cmd;
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Setup valid state
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled by initialization
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled by initialization
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markContextInvalid();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        assertTrue(events.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown exception");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be IllegalStateException");
    }
}
