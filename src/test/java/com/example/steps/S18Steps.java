package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private Command lastCommand;
    private List<DomainEvent> lastResult;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context is stored in the command construction in the 'When' step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context is stored in the command construction in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-violate-auth");
        // In this domain logic, null/blank teller ID simulates lack of auth
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-violate-timeout");
        aggregate.markAsTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-violate-nav");
        // Context violation simulated by null terminal ID in command
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Create command. 
            // For the 'valid' scenario, we use valid data.
            // For the 'violation' scenarios, the aggregate state or nulls (derived from Givens) will trigger errors.
            String tid = (aggregate.id().equals("session-violate-auth")) ? "" : "teller-1";
            String trm = (aggregate.id().equals("session-violate-nav")) ? "" : "term-1";
            
            // If it's the valid session, ensure data is populated
            if (aggregate.id().equals("session-123")) {
                tid = "teller-1";
                trm = "term-1";
            }

            lastCommand = new StartSessionCmd(aggregate.id(), tid, trm);
            lastResult = aggregate.execute(lastCommand);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            lastResult = null;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(lastResult, "Expected result list, but got null (exception might have occurred)");
        assertEquals(1, lastResult.size());
        assertTrue(lastResult.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) lastResult.get(0);
        assertEquals("session.started", event.type());
        assertEquals("teller-1", event.tellerId());
        assertEquals("term-1", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but command succeeded.");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException, 
            "Expected domain error (IllegalArgumentException/IllegalStateException), but got: " + capturedException.getClass().getSimpleName());
    }
}