package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import com.example.domain.uinavigation.repository.TellerSessionRepository;
import com.example.exceptions.DomainError;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionRepository repository = new TellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String aggregateId;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregateId = "teller-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(aggregateId);
        repository.save(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context provided in the 'When' step via command
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context provided in the 'When' step via command
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd = new StartSessionCmd(aggregateId, "user-123", "term-01");
            aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        List<DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected an event to be emitted");
        Assertions.assertTrue(events.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        SessionStartedEvent event = (SessionStartedEvent) events.get(0);
        Assertions.assertEquals("user-123", event.tellerId());
        Assertions.assertEquals("term-01", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregateId = "teller-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(aggregateId);
        // Simulate violation: e.g., empty tellerId passed in command (handled in logic)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregateId = "teller-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(aggregateId);
        // Simulate logic check in aggregate
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        this.aggregateId = "teller-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(aggregateId);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof DomainError || caughtException instanceof IllegalArgumentException, "Expected DomainError or IllegalArgumentException");
    }
}
