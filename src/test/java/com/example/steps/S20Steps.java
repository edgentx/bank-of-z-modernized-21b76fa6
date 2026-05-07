package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.TellerSessionState;
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
        // Create an aggregate in a valid, active, authenticated state
        aggregate = new TellerSessionAggregate("session-123");
        // Hydrate state manually to simulate an existing active session
        aggregate.applySessionCreated("session-123", "teller-456", Instant.now().minusSeconds(60));
        aggregate.applyAuthenticated("teller-456");
        aggregate.applyNavigationStateUpdated("MAIN_MENU");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate initialization in the previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Created but not authenticated
        aggregate.applySessionCreated("session-401", "teller-456", Instant.now());
        // Intentionally skipping applyAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        // Simulate a session created 2 hours ago (assuming timeout < 2 hours)
        aggregate.applySessionCreated("session-408", "teller-456", Instant.now().minus(Duration.ofHours(2)));
        aggregate.applyAuthenticated("teller-456");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-500");
        aggregate.applySessionCreated("session-500", "teller-456", Instant.now());
        aggregate.applyAuthenticated("teller-456");
        // Force state to a value that might indicate an invalid context for ending (e.g. locked/processing)
        aggregate.applyNavigationStateUpdated("PROCESSING_CRITICAL_TRANSACTION");
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
        assertEquals(SessionEndedEvent.class, resultEvents.get(0).getClass());
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect either IllegalStateException or IllegalArgumentException depending on invariant violation type
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
