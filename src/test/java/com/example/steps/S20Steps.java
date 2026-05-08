package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellering.model.EndSessionCmd;
import com.example.domain.tellering.model.SessionEndedEvent;
import com.example.domain.tellering.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // ID defaults to "test-session", Auth defaults to true, Active defaults to true
        this.aggregate = new TellerSessionAggregate("test-session");
        // Manually simulate the aggregate being in an active, authenticated state
        // In a real flow, we would load from events, but for unit testing we manipulate state or execute init cmd.
        // Here we assume the constructor/defaults cover the 'valid' state for simplicity.
        aggregate.activate(); // Helper method to simulate an active session for testing purposes
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the aggregate ID "test-session"
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd("test-session");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("test-session");
        aggregate.forceUnauthenticated(); // Helper to simulate violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("test-session");
        aggregate.forceTimeout(); // Helper to simulate violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("test-session");
        aggregate.invalidateNavigationState(); // Helper to simulate violation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Check it's an IllegalStateException (Domain Error)
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertTrue(((IllegalStateException) thrownException).getMessage().contains("Cannot end session"));
    }
}
