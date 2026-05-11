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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "sess-123";
        // Simulate a live, authenticated session that started recently
        Instant startedAt = Instant.now().minusSeconds(60);
        this.aggregate = new TellerSessionAggregate(sessionId, "teller-001", true, startedAt);
        // Initialize navigation state (e.g., HOME_SCREEN)
        // Note: We rely on the aggregate's test constructor or ability to set state for testing.
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicit in the aggregate initialization above.
        // In a real runner, this might involve pulling from a context map.
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "sess-unauth";
        Instant startedAt = Instant.now().minusSeconds(60);
        // Create session where authenticated = false
        this.aggregate = new TellerSessionAggregate(sessionId, "teller-001", false, startedAt);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        // Create session that started 5 hours ago (assuming configured timeout is shorter)
        Instant startedAt = Instant.now().minus(Duration.ofHours(5));
        this.aggregate = new TellerSessionAggregate(sessionId, "teller-001", true, startedAt);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = "sess-nav-error";
        Instant startedAt = Instant.now().minusSeconds(60);
        this.aggregate = new TellerSessionAggregate(sessionId, "teller-001", true, startedAt);
        // Manually mark state as inconsistent for this test scenario
        // (In a real app, this might happen via a corruption event or unfinished txn)
        this.aggregate.markNavigationStateInconsistent();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        Assertions.assertNull(resultEvents, "No events should be emitted when command is rejected");
    }
}
