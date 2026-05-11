package com.example.steps;

import com.example.domain.tellersession.model.*;
import com.example.domain.shared.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private EndSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Assume constructor handles necessary initialization
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        cmd = new EndSessionCmd("session-123");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    // --- Scenarios for violations (placeholders based on requirements) ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        // In a real implementation, we would set a state flag to 'unauthenticated'
        // and the execute method would check it.
        aggregate = new TellerSessionAggregate("session-no-auth");
        aggregate.setAuthenticated(false); // Hypothetical setter for invariant testing
        cmd = new EndSessionCmd("session-no-auth");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setTimedOut(true); // Hypothetical setter
        cmd = new EndSessionCmd("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.setNavigationStateInvalid(true); // Hypothetical setter
        cmd = new EndSessionCmd("session-bad-nav");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        assertNull(resultEvents);
    }
}
