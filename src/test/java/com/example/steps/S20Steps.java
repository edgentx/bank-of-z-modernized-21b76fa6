package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // By default in our test setup, we ensure it is authenticated to be "valid"
        this.aggregate.markAuthenticated();
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is set in the previous step
        Assertions.assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly mark as unauthenticated to violate the invariant
        this.aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        // Force the session to appear stale
        this.aggregate.markStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "session-nav-locked";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        // Lock navigation to violate the invariant
        this.aggregate.markNavigationLocked();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId, "teller-001");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.ended", event.type());
        Assertions.assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
