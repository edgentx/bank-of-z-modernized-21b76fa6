package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.uimodel.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Seed state to make it valid for an active session
        aggregate.markAuthenticated("teller123");
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the UUID generation in the previous step
        // Ensuring aggregate is loaded
        if (aggregate == null) {
            a_valid_teller_session_aggregate();
        }
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Do NOT mark authenticated - default state is unauthenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated("teller123");
        // Simulate timeout
        aggregate.markTimedOut();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated("teller123");
        // Simulate invalid navigation state
        aggregate.markNavigationInvalid();
        repository.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new com.example.domain.tellersession.model.EndSessionCmd(aggregate.id());
            List<DomainEvent> events = aggregate.execute(cmd);
            // Commit changes in aggregate (simulated)
            repository.save(aggregate);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        List<DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        Assertions.assertTrue(events.get(0) instanceof SessionEndedEvent, "Expected SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Typically IllegalStateException or a specific DomainException
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
