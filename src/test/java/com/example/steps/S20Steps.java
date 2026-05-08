package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermgmt.model.EndSessionCmd;
import com.example.domain.tellermgmt.model.SessionEndedEvent;
import com.example.domain.tellermgmt.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate an active session by setting internal state directly for testing
        aggregate.setAuthenticatedTeller("teller-1");
        aggregate.setSessionStart(Instant.now().minusSeconds(60));
        aggregate.setLastActivity(Instant.now().minusSeconds(10));
        aggregate.setActive(true);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate constructor in the previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-404");
        // Simulate an unauthenticated session state
        aggregate.setAuthenticatedTeller(null);
        aggregate.setActive(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticatedTeller("teller-1");
        // Simulate a session that started too long ago
        aggregate.setSessionStart(Instant.now().minus(Duration.ofHours(2)));
        aggregate.setLastActivity(Instant.now().minus(Duration.ofHours(2)));
        aggregate.setActive(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.setAuthenticatedTeller("teller-1");
        aggregate.setSessionStart(Instant.now());
        aggregate.setLastActivity(Instant.now());
        aggregate.setActive(true);
        // Simulate a state where the terminal is busy (in-transaction)
        aggregate.setTerminalInTransaction(true);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
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
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect either IllegalStateException or IllegalArgumentException depending on specific violation context
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
