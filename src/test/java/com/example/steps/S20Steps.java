package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Throwable caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Initialize state to valid defaults required for a successful end
        aggregate.applyEvent(new SessionStartedEvent("session-123", "teller-1", "terminal-1", Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The sessionId is implicitly handled by the aggregate instance ID in this test setup
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultingEvents = aggregate.execute(cmd);
        } catch (DomainException | IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertEquals("SessionEndedEvent", resultingEvents.get(0).type());
        Assertions.assertEquals(aggregate.id(), resultingEvents.get(0).aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate("session-no-auth");
        // No SessionStartedEvent applied -> not authenticated
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // We expect IllegalStateException or a custom DomainException wrapping the invariant violation
        // The aggregate throws IllegalStateException with the message
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Start session long time ago (more than 30 minutes)
        Instant past = Instant.now().minus(Duration.ofMinutes(45));
        aggregate.applyEvent(new SessionStartedEvent("session-timeout", "teller-1", "terminal-1", past));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        // Start valid session
        aggregate.applyEvent(new SessionStartedEvent("session-bad-nav", "teller-1", "terminal-1", Instant.now()));
        // Transition to a transactional state (e.g., CASH_DEPOSIT) which cannot be ended immediately
        aggregate.applyEvent(new NavigationChangedEvent("session-bad-nav", "CASH_DEPOSIT", "/deposit/input", Instant.now()));
    }

}
