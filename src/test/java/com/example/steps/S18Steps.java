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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.clearEvents();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Command construction happens in 'When'
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Command construction happens in 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            String id = aggregate.id();
            command = new StartSessionCmd(id, "teller-123", "term-456");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("teller-123", event.tellerId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markUnauthenticated();
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated();
        aggregate.markTimedOut();
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated();
        aggregate.markInvalidContext();
        aggregate.clearEvents();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception but none was thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
