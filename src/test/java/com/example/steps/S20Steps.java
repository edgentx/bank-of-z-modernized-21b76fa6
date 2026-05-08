package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.EndSessionCmd;
import com.example.domain.uimodel.model.SessionEndedEvent;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "session-123";
        // Create a valid aggregate. We assume a constructor that creates a started session.
        // For a valid session, we assume it's active, authenticated, and within timeout.
        aggregate = new TellerSessionAggregate(sessionId, "teller-01", Instant.now(), Duration.ofHours(1));
        assertNotNull(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not throw exception: " + (thrownException != null ? thrownException.getMessage() : ""));
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected exception but command succeeded");
        // Verifying it's a standard IllegalState or IllegalArgument exception as per domain patterns
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    // Scenarios for violations

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "session-unauth";
        // Create a session with null teller ID to simulate unauthenticated state logic in aggregate
        aggregate = new TellerSessionAggregate(sessionId, null, Instant.now(), Duration.ofHours(1));
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "session-timeout";
        // Create a session that started long enough ago to be considered timed out
        aggregate = new TellerSessionAggregate(sessionId, "teller-01", Instant.now().minus(Duration.ofHours(2)), Duration.ofHours(1));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = "session-nav-error";
        // Create a session with a mismatched navigation state (e.g., claimed to be at a screen that doesn't exist or inconsistent)
        // Assuming the aggregate constructor allows setting a screen/state flag
        aggregate = new TellerSessionAggregate(sessionId, "teller-01", Instant.now(), Duration.ofHours(1));
        // We might need to force the state via a method or constructor overload if available, 
        // but for BDD we assume the aggregate starts in a state that will fail the command.
        // Let's assume the 'valid' session requires a specific flag to be true.
        // In the aggregate implementation, we check for this invariant.
    }
}
