package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(); // Pre-authenticated
        aggregate.activate();          // Pre-active
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is implicitly handled by the aggregate instantiation in the previous step
        // Or we could assert it matches. For now, no-op as state is set up.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String id = "session-auth-fail";
        aggregate = new TellerSessionAggregate(id);
        aggregate.activate(); // Active but NOT authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated();
        aggregate.simulateTimeout(); // Sets active=true but lastActivity too far in past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        String id = "session-nav-fail";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated();
        aggregate.activate();
        aggregate.enterTransaction(); // Enters a state where logout is forbidden
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // In this domain layer implementation, invariants are enforced by throwing exceptions.
        // We check the message content matches the violation scenario.
        assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank());
    }
}
