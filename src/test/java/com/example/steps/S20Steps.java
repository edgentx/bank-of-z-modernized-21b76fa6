package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
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
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate a prior session start event to make it valid
        aggregate.loadFromHistory(List.of(
            new SessionStartedEvent("session-123", "teller-1", Instant.now())
        ));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is implied by the aggregate ID in this test setup
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Create a session that was never started (authenticated)
        aggregate = new TellerSessionAggregate("session-invalid-auth");
        // Do not apply SessionStartedEvent
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        Instant past = Instant.now().minus(Duration.ofMinutes(30)); // Assuming timeout < 30 mins
        aggregate.loadFromHistory(List.of(
            new SessionStartedEvent("session-timeout", "teller-1", past)
        ));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.loadFromHistory(List.of(
            new SessionStartedEvent("session-bad-nav", "teller-1", Instant.now())
        ));
        // Simulate a drift in navigation state (e.g. in a transaction but thinks it's at menu)
        aggregate.setNavigationStateDrift(true);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // We expect IllegalStateException or IllegalArgumentException for domain violations
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || 
            thrownException instanceof IllegalArgumentException
        );
    }
}
