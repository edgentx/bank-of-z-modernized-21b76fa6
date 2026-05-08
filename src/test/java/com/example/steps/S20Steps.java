package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
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
    private EndSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state: Authenticated, Active, IDLE, Recent Activity
        aggregate.markAuthenticated("teller-007");
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The ID is set in the aggregate initialization, but we prepare the command here
        this.command = new EndSessionCmd("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        this.aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.markInactive(); // Not authenticated
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-007");
        aggregate.setNavigationState("IDLE");
        // Set activity to 31 minutes ago (threshold is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSessionAggregate("session-badnav");
        aggregate.markAuthenticated("teller-007");
        // Set state to something other than IDLE (e.g. TRANSACTION_IN_PROGRESS)
        aggregate.setNavigationState("CASH_DEPOSIT_SCREEN");
        aggregate.setLastActivityAt(Instant.now());
    }

    // --- Whens ---

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            // Ensure the command ID matches the aggregate ID for the test
            if (command == null) {
                command = new EndSessionCmd(aggregate.id());
            }
            this.resultEvents = aggregate.execute(command);
            this.capturedException = null;
        } catch (Exception e) {
            this.capturedException = e;
            this.resultEvents = null;
        }
    }

    // --- Thens ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted, but got null (likely an exception occurred)");
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive after command");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected a domain exception, but command succeeded");
        assertTrue(capturedException instanceof RuntimeException, "Expected a RuntimeException subclass");
    }
}
