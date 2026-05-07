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
import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {
    private TellerSessionAggregate aggregate;
    private String sessionId = "session-1";
    private String tellerId = "teller-1";
    private String terminalId = "terminal-A";
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Valid aggregate implies an authenticated teller ready to start a session
        aggregate = new TellerSessionAggregate(sessionId, true);
        caughtException = null;
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Use default valid tellerId
        assertNotNull(tellerId);
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Use default valid terminalId
        assertNotNull(terminalId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Unauthenticated aggregate
        aggregate = new TellerSessionAggregate(sessionId, false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Authenticated, but logically timed out
        aggregate = new TellerSessionAggregate(sessionId, true);
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Authenticated, but we will provide a null/blank terminalId in the command to simulate invalid nav state
        aggregate = new TellerSessionAggregate(sessionId, true);
        this.terminalId = ""; // Invalid
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
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
        assertEquals(sessionId, event.aggregateId());
        assertEquals("session.started", event.type());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception but command succeeded");
        // Verify it's a validation/domain logic exception (IllegalStateException fits the pattern used)
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
