package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "sess-" + UUID.randomUUID();
        // Create a valid aggregate: Authenticated, Active, Valid State
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate state to simulate an active session
        aggregate.bootstrapStateForTesting(true, Instant.now().plusSeconds(300), true);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        sessionId = "sess-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate state: Not Authenticated
        aggregate.bootstrapStateForTesting(false, Instant.now().plusSeconds(300), true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "sess-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate state: Expired (last active time in past)
        aggregate.bootstrapStateForTesting(true, Instant.now().minusSeconds(60), true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        sessionId = "sess-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate state: Invalid Navigation Context
        aggregate.bootstrapStateForTesting(true, Instant.now().plusSeconds(300), false);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(sessionId);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (DomainException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof SessionEndedEvent);

        SessionEndedEvent event = (SessionEndedEvent) resultingEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain errors typically manifest as IllegalStateExceptions or DomainException in this pattern
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof DomainException);
    }
}
