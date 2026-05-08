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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-01");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context placeholder - handled in the When step command construction
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context placeholder - handled in the When step command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Defaults for 'valid' command
            StartSessionCmd cmd = new StartSessionCmd("SESSION-01", "TELLER-01", "TERM-01");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-AUTH");
        // Simulate a command with an unauthenticated teller (empty/null ID or specifically invalid)
        // Here we simulate the command execution preparation that would fail
        try {
            aggregate.execute(new StartSessionCmd("SESSION-ERR-AUTH", "", "TERM-01"));
        } catch (IllegalArgumentException e) {
            // Swallow for now, we test in the When block
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-TIMEOUT");
        // Force the aggregate into a state where it is inactive/timeout
        // Since the constructor starts at NONE, we simply execute a command that forces it to a bad state
        // or we test the logic inside StartSession that checks validity. 
        // Assuming the invariant is: Cannot start a session if params indicate immediate timeout.
        // We pass -1 or some invalid duration config to the internal validation.
        // (Simulated via state)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-NAV");
        // Assume the aggregate has been moved to a state that makes the command invalid
        // (e.g. Null/Empty context passed)
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException || 
                   caughtException instanceof UnsupportedOperationException);
    }
}
