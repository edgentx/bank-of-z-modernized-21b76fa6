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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String testTellerId = "teller-123";
    private String testTerminalId = "term-456";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-abc");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        testTellerId = "teller-123";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        testTerminalId = "term-456";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-abc");
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-abc");
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("session-abc");
        aggregate.markNavigationInvalid();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        Command cmd = new StartSessionCmd(aggregate.id(), testTellerId, testTerminalId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent);
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals("session.started", started.type());
        assertEquals(aggregate.id(), started.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
