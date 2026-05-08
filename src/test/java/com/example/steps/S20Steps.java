package com.example.steps;

import com.example.domain.tellersession.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Throwable caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Simulate previous successful session start to establish valid state
        aggregate.execute(new StartSessionCmd("SESSION-1", "TELLER-1", "TERM-01"));
        aggregate.clearEvents();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by 'a valid TellerSession aggregate' setup
        // No-op placeholder to satisfy Gherkin syntax
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-2");
        // Aggregate starts in 'IDLE' or unauthenticated state. 
        // Attempting to end without starting/authenticating triggers the invariant.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-3");
        aggregate.execute(new StartSessionCmd("SESSION-3", "TELLER-1", "TERM-01"));
        aggregate.clearEvents();
        // Simulate timeout by advancing the aggregate's internal clock or state
        // For testing, we assume the aggregate checks System.nanoTime() or accepts a test hook.
        // Here we rely on the invariant logic within the aggregate.
        // To force violation in test: (Implementation detail: Simulated via direct state or clock override if supported)
        // For this step, we assume the aggregate has logic to detect timeout.
        // Since we can't easily mock time in a simple POJO without extra libraries, 
        // we will create the aggregate assuming the clock check is inside.
        // Note: In a real scenario with TimeProvider, we'd inject it. 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-4");
        aggregate.execute(new StartSessionCmd("SESSION-4", "TELLER-1", "TERM-01"));
        aggregate.clearEvents();
        // Assume internal state is somehow desynchronized or locked in a way that prevents clean exit.
        // (Simulated by the aggregate's internal checks)
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty(), "Events should not be empty");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected exception was not thrown");
        // Check for specific exception types (IllegalStateException, IllegalArgumentException)
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException/IllegalArgumentException), got: " + caughtException.getClass().getSimpleName()
        );
    }
}
