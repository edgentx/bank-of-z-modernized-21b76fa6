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
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "teller-123";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "term-456";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "teller-123";
        this.terminalId = "term-456";
        // To violate authentication, we can simulate a state where auth is missing/pending,
        // but in this simple model, we pass an authenticated flag or rely on external context.
        // For the test to fail as expected, we pass invalid auth data or assume the command handles it.
        // Here, we rely on the command's boolean flag.
        // We'll set up the command execution to fail.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "teller-123";
        this.terminalId = "term-456";
        // Timeout logic depends on last active timestamp. Since this is a new aggregate,
        // standard StartSession shouldn't fail timeout unless logic checks specific constraints.
        // We will trigger the command to verify the invariant logic.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        this.tellerId = "teller-123";
        this.terminalId = "term-456";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Determine valid auth flag based on scenario context (heuristic)
            boolean isAuthenticated = !caughtException.getClass().getName().contains("authentication");
            
            Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_StartSessionCmd_command_is_executed_with_invalid_auth() {
        try {
            Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId, false); // Force failure
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // It should be an IllegalStateException or similar domain error
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
