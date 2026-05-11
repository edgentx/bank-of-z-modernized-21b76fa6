package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step Definitions for S-20: EndSessionCmd
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd command;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.setNavigationState("MAIN_MENU"); // Ensure valid state
        aggregate.touch(); // Ensure activity
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-invalid-auth");
        // Do not mark authenticated, stays false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-invalid-timeout");
        aggregate.markAuthenticated();
        aggregate.markTimedOut(); // Set timestamp to past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-invalid-nav");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS"); // Invalid state for logout
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Using the ID from the aggregate created in previous step
        // Realistically, the command DTO is created with this ID
    }

    @When("the EndSessionCmd command is executed")
    public void theEndSessionCmdCommandIsExecuted() {
        try {
            command = new EndSessionCmd(aggregate.id());
            resultingEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void aSessionEndedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "One event should be emitted");

        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event must be SessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());

        // Ensure aggregate state reflects the end
        assertFalse(aggregate.isActive(), "Aggregate should be inactive");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Exception should be a domain error (IllegalStateException)");
    }

    // --- Additional Test Runner for S-20 if run standalone or via Suite ---
    // This configuration can be placed in a separate S20TestSuite.java if preferred,
    // but including here for completeness of the feature test file context.
    /*
    @RunWith(Cucumber.class)
    @CucumberOptions(features = "features/S-20.feature", plugin = {"pretty"})
    public class S20TestSuite {}
    */
}
