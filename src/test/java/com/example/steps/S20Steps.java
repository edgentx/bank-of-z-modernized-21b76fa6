package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellerauth.model.EndSessionCmd;
import com.example.domain.tellerauth.model.SessionEndedEvent;
import com.example.domain.tellerauth.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "SESSION-123";
        Instant now = Instant.now();
        // Bypassing command for setup to ensure valid state for success scenario
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulating a previously started session
        aggregate.markAsAuthenticated("TELLER-001");
        aggregate.markAsActive(now);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // ID is handled in the previous step
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("session.ended", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "SESSION-INVALID-AUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated("TELLER-001");
        // Set last active time to 2 hours ago (assuming timeout is 1 hour)
        aggregate.markAsActive(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "SESSION-INVALID-NAV";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated("TELLER-001");
        aggregate.markAsActive(Instant.now());
        // Simulate an inconsistent navigation state where aggregate thinks it's in one state
        // but internal state might be corrupt or conflicting (simulated via a flag or logic check)
        // Here we use a method to corrupt the state for testing purposes
        aggregate.markNavigationStateInconsistent();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_a_teller_must_be_authenticated_to_initiate_a_session() {
        String sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsActive(Instant.now()); // Active but not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_sessions_must_timeout_after_a_configured_period_of_inactivity() {
        String sessionId = "SESSION-STALE";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated("TELLER-001");
        aggregate.markAsActive(Instant.now().minusSeconds(3600)); // > default timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state_must_accurately_reflect_the_current_operational_context() {
        String sessionId = "SESSION-BAD-NAV";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated("TELLER-001");
        aggregate.markAsActive(Instant.now());
        aggregate.markNavigationStateInconsistent();
    }
}
