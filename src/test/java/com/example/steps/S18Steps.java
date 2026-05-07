package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: Implement StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String validTellerId = "TELLER_123";
    private String validTerminalId = "TERM_01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_001");
        aggregate.markAuthenticated(); // Default to authenticated for positive path
        caughtException = null;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Variables are initialized, logic handled in 'When'
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Variables are initialized, logic handled in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(validTellerId, validTerminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        assertEquals("session.started", resultEvents.get(0).type());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION_ERR_AUTH");
        aggregate.markUnauthenticated(); // Violate invariant
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_ERR_TIMEOUT");
        aggregate.markAuthenticated(); // Authenticated
        aggregate.markTimedOut();      // But timed out
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_ERR_NAV");
        aggregate.markAuthenticated(); // Authenticated
        aggregate.markActive();        // Violate invariant: already active
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
        // Optional: Check specific message content if strict validation is needed
        assertFalse(caughtException.getMessage().isBlank(), "Error message should not be blank");
    }
}
