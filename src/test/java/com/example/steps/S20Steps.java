package com.example.steps;

import com.example.domain.navigation.model.EndSessionCmd;
import com.example.domain.navigation.model.SessionEndedEvent;
import com.example.domain.navigation.model.TellerSessionAggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "sess-" + UUID.randomUUID();
        // Create a fresh aggregate. In a real scenario, we might hydrate it from events.
        // Here we instantiate directly. To make it "valid" for ending, we assume it's active.
        // We manually set state via constructor or rehydration logic for the test context.
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate an active session state (since we don't have StartSession here)
        aggregate.markActive(); 
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The sessionId is already set in the previous step or explicitly here.
        Assertions.assertNotNull(this.sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(this.sessionId);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals(this.sessionId, event.aggregateId());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "sess-unauth";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Leaving it inactive (unauthenticated) violates the invariant for ending an active session
        // or rather, you can't end a session that isn't properly authenticated/active.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate a timed-out session state
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "sess-nav-error";
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Simulate an inconsistent state
        aggregate.markNavigationError();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Depending on implementation, could be IllegalStateException or a custom DomainException
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
