package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Placeholder - command construction handles this
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Placeholder - command construction handles this
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Default valid command if not overridden by a 'Given... violates' step
        if (command == null) {
            command = new StartSessionCmd("session-123", "teller-1", "TERM-01", true, "HOME", Instant.now());
        }

        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Command constructs with isAuthenticated = false
        command = new StartSessionCmd("session-auth-fail", "teller-1", "TERM-01", false, "HOME", Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // To simulate a violation of timeout, we might pass a null timestamp or a timestamp in the far past.
        // The aggregate logic checks for null timestamp in this implementation to represent invalid state.
        command = new StartSessionCmd("session-timeout-fail", "teller-1", "TERM-01", true, "HOME", null);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Command constructs with blank/invalid operational context
        command = new StartSessionCmd("session-nav-fail", "teller-1", "TERM-01", true, "", Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // Verify it's an IllegalStateException or IllegalArgumentException (domain error)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Exception should be a domain error (IllegalState or IllegalArgument)");
    }
}
