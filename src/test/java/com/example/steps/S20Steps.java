package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.EndSessionCmd;
import com.example.domain.uimodel.TellerSessionAggregate;
import com.example.domain.uimodel.TellerSessionEndedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1: Valid Session
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup a valid, authenticated state (implicitly via a factory helper here)
        aggregate.markAuthenticated();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The aggregate is already initialized with a valid ID in the previous step
        assertNotNull(aggregate.id());
    }

    // Scenario 2: Auth Violation
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Default constructor state implies not authenticated
    }

    // Scenario 3: Timeout Violation
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markTimedOut();
    }

    // Scenario 4: Navigation State Violation
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markInvalidNavigationState();
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TellerSessionEndedEvent);

        TellerSessionEndedEvent event = (TellerSessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In this simple domain model, we use standard exceptions (IllegalStateException)
        // to represent domain rejections.
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
