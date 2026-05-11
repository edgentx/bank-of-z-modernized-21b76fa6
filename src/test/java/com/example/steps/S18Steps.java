package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private Throwable thrownException;
    private java.util.List<com.example.domain.shared.DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // The violation will be in the command executed in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        // In a real system we would manipulate the clock or aggregate state to simulate timeout.
        // For this stub, the exception expectation is key.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        this.aggregate = new TellerSessionAggregate("session-nav-fail");
        // The violation will be in the command (invalid terminal context)
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in command construction below, or stored for verification
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in command construction below
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Determine context based on previous Givens
        // If aggregate ID indicates auth fail scenario
        boolean isAuthenticated = !aggregate.id().equals("session-auth-fail");
        // If aggregate ID indicates nav fail scenario
        boolean isActiveTerminal = !aggregate.id().equals("session-nav-fail");

        this.command = new StartSessionCmd(
            "teller-1", 
            "terminal-1", 
            isAuthenticated, 
            isActiveTerminal
        );

        try {
            this.resultingEvents = aggregate.execute(command);
        } catch (Throwable t) {
            this.thrownException = t;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // We expect an IllegalStateException for domain invariant violations
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        
        // Specific message checks based on the scenario
        if (aggregate.id().equals("session-auth-fail")) {
            Assertions.assertTrue(thrownException.getMessage().contains("authenticated"));
        } else if (aggregate.id().equals("session-nav-fail")) {
            Assertions.assertTrue(thrownException.getMessage().contains("Navigation state"));
        }
    }
}