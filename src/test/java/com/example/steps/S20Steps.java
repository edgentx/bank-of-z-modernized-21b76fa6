package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup a healthy, authenticated, active session
        Instant now = Instant.now();
        aggregate = new TellerSessionAggregate("session-123");
        // Bypassing command handler for setup speed in test, or just rely on clean state logic
        // We manually set state for the 'valid' scenario
        aggregate.setTellerId("teller-01");
        aggregate.setAuthenticated(true);
        aggregate.setSessionStart(now.minus(Duration.ofMinutes(5))); // Active 5 mins
        aggregate.setLastActivity(now.minusSeconds(10)); // Active 10 secs ago
        aggregate.setCurrentContext("MAIN_MENU"); // Valid context
        aggregate.markActive(); // Sets active = true
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the aggregate instance 'session-123'
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd("session-123");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("session.ended", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-bad-auth");
        aggregate.setTellerId("teller-01");
        aggregate.setAuthenticated(false); // Violation
        aggregate.markActive();
        aggregate.setCurrentContext("MAIN_MENU");
        aggregate.setSessionStart(Instant.now().minusMinutes(1));
        aggregate.setLastActivity(Instant.now().minusSeconds(10));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setTellerId("teller-02");
        aggregate.setAuthenticated(true);
        aggregate.markActive();
        aggregate.setCurrentContext("MAIN_MENU");
        aggregate.setSessionStart(Instant.now().minusHours(1));
        // Violation: Last activity 16 minutes ago (Timeout is 15)
        aggregate.setLastActivity(Instant.now().minusMinutes(16));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.setTellerId("teller-03");
        aggregate.setAuthenticated(true);
        aggregate.markActive();
        aggregate.setSessionStart(Instant.now().minusMinutes(1));
        aggregate.setLastActivity(Instant.now().minusSeconds(10));
        aggregate.setCurrentContext("INVALID_STATE"); // Violation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception but command succeeded");
        // Depending on implementation, could be IllegalStateException or IllegalArgumentException.
        // Domain errors in this framework are typically RuntimeExceptions.
        Assertions.assertTrue(caughtException instanceof RuntimeException);
    }
}
