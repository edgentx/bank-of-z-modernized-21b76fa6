package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state
        aggregate.markAuthenticated();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId already initialized in previous step
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Deliberately NOT calling markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Auth valid
        aggregate.simulateTimeout();    // But timed out
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Auth valid
        aggregate.invalidateNavigationState(); // State invalid
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertEquals(1, events.size());
        DomainEvent event = events.get(0);
        assertTrue(event instanceof SessionEndedEvent);
        SessionEndedEvent ended = (SessionEndedEvent) event;
        assertEquals("session.ended", ended.type());
        assertEquals(sessionId, ended.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We expect IllegalStateException for domain rule violations
        assertTrue(caughtException instanceof IllegalStateException 
                   || caughtException instanceof UnknownCommandException); 
    }
}
