package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Throwable caughtException;
    private StartSessionCmd pendingCmd;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        if (pendingCmd == null) {
            pendingCmd = new StartSessionCmd("session-123", "teller-101", "terminal-A", Instant.now());
        }
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        if (pendingCmd == null) {
            pendingCmd = new StartSessionCmd("session-123", "teller-101", "terminal-A", Instant.now());
        }
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        pendingCmd = new StartSessionCmd("session-401", "", "terminal-A", Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Expired timestamp
        pendingCmd = new StartSessionCmd("session-timeout", "teller-101", "terminal-A", Instant.now().minusSeconds(3600));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        pendingCmd = new StartSessionCmd("session-bad-nav", "teller-101", null, Instant.now());
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(pendingCmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Could assert specific message types depending on implementation details
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }
}
