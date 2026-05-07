package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private Exception thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        String tellerId = "teller-01";
        Instant now = Instant.now();
        // Create valid aggregate via internal state reflection or factory
        aggregate = new TellerSessionAggregate(sessionId);
        // Force into a valid open state for testing
        aggregate.markInitialized(tellerId, now);
        aggregate.updateLastActivity(now);
        aggregate.setNavigationState("IDLE");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The sessionId is already part of the aggregate instance in this context
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Aggregate created but not initialized (tellerId is null)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markInitialized("teller-01", Instant.now().minus(Duration.ofHours(2)));
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(2)));
        aggregate.setNavigationState("IDLE");
        // Assume timeout is configured for 15 minutes, so this is expired.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        String sessionId = "session-nav-error";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markInitialized("teller-01", Instant.now());
        aggregate.updateLastActivity(Instant.now());
        aggregate.setNavigationState("INVALID_STATE_FOR_TERMINATION");
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception but command succeeded");
        // Check if it's an IllegalStateException or IllegalArgumentException (domain error)
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException/IllegalArgumentException), got: " + thrownException.getClass()
        );
    }
}
