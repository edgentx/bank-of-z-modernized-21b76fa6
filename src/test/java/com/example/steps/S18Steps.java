package com.example.steps;

import com.example.domain.tellersession.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Pre-authenticate the session to make it valid for the happy path
        aggregate.authenticate("teller-01"); 
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled within the command construction in the When step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled within the command construction in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, "term-42", "teller-01");
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
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("term-42", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "TS-401";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling authenticate()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "TS-408";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("teller-01");
        aggregate.markAsStale(); 
        // Force the aggregate to think it timed out (simulate time passing)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "TS-409";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("teller-01");
        aggregate.corruptNavigationState(); // Simulate state mismatch
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect either an IllegalStateException or IllegalArgumentException depending on specific invariant logic
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_failing() {
         try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, "term-42", "teller-01");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted_verify() {
        a_session_started_event_is_emitted(); // Reuse logic
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_verify() {
        the_command_is_rejected_with_a_domain_error(); // Reuse logic
    }

}
