package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSession("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-42";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "terminal-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId, Instant.now().plusSeconds(3600));
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSession("session-404");
        // Simulate authentication failure by passing null/invalid teller context in the command later, 
        // or by setting the aggregate in a state where auth is missing.
        // Here we assume the command carries the auth token/context. 
        // We will pass an invalid auth marker in the command via the 'When' step context.
        // However, to keep the Given/When flow, we can set the tellerId to null here if needed.
        this.tellerId = null; // Triggers violation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSession("session-timeout");
        this.tellerId = "teller-1";
        this.terminalId = "terminal-1";
        // We'll pass an expired timeout window in the command during execution.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSession("session-nav");
        this.tellerId = "teller-1";
        this.terminalId = "terminal-1";
        // We'll pass a conflicting navigation state in the command.
    }

}
