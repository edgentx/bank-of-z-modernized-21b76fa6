package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.tellermocking.model.TellerSessionAggregate;
import com.example.domain.tellermocking.model.StartSessionCmd;
import com.example.domain.tellermocking.model.SessionStartedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private Aggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup state to be valid by default
        ((TellerSessionAggregate) this.aggregate).markAuthenticated();
        ((TellerSessionAggregate) this.aggregate).setTerminalContext("term-456");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Command created in When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Command created in When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // Deliberately not calling markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        ((TellerSessionAggregate) this.aggregate).markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        // Deliberately not setting terminal context or invalidating it
        ((TellerSessionAggregate) this.aggregate).markAuthenticated();
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            String sessionId = aggregate.id();
            // Using hardcoded values for simplicity in test, could be scenario-specific
            this.command = new StartSessionCmd(sessionId, "teller-1", "terminal-1");
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
