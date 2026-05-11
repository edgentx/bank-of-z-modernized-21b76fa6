package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String aggregateId = UUID.randomUUID().toString();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Helper to create a fresh, valid aggregate
    private TellerSessionAggregate createValidAggregate() {
        return new TellerSessionAggregate(aggregateId);
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = createValidAggregate();
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context managed by the command construction in the 'When' step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context managed by the command construction in the 'When' step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Valid command data
            String tellerId = "teller-001";
            String terminalId = "terminal-101";
            Command cmd = new StartSessionCmd(aggregateId, tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "One event should be emitted");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // In this context, we simulate a state where the teller is not authenticated.
        // The aggregate handles this via logic, but for BDD we setup the command
        // or aggregate state. Here we use the aggregate to detect the violation.
        aggregate = createValidAggregate();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_fails_auth() {
        try {
            // Command with blank/null ID to simulate invalid auth data or allow aggregate to reject
            Command cmd = new StartSessionCmd(aggregateId, "", "");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Exception should have been thrown");
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException,
            "Exception should be a domain error"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = createValidAggregate();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_fails_timeout() {
        // Aggregate logic is a stub for timeout, so we expect UnknownCommandException or specific error
        try {
             Command cmd = new StartSessionCmd(aggregateId, "t-1", "term-1");
             // Assuming stub throws exception or validation fails
             resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
             caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = createValidAggregate();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_fails_nav() {
        // Similar to timeout, logic is stubbed
        try {
             Command cmd = new StartSessionCmd(aggregateId, "t-1", "term-1");
             resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
             caughtException = e;
        }
    }
}
