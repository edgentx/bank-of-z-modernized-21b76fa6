package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import com.example.domain.uinavigation.command.StartSessionCmd;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true); // Assume valid means authenticated
        aggregate.setState(TellerSessionAggregate.SessionState.IDLE);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Data setup handled in the When step via the command object
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Data setup handled in the When step via the command object
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-1", "terminal-A");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("teller-1", event.tellerId());
        Assertions.assertEquals("terminal-A", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(false); // Not authenticated
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setState(TellerSessionAggregate.SessionState.TIMED_OUT);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setState(TellerSessionAggregate.SessionState.INVALID_CONTEXT);
    }
}
