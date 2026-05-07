package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Setup valid state
        aggregate.markAuthenticated(true);
        aggregate.markActive(true);
        aggregate.setLastActivityAt(Instant.now()); // Fresh
        aggregate.setStateChecksum("valid-checksum-123");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled in the aggregate initialization or command creation
        // We just ensure the aggregate ID is set, which it is in the constructor
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Setup invalid state: Not authenticated
        aggregate.markAuthenticated(false);
        aggregate.markActive(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setStateChecksum("valid-checksum");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);

        // Setup invalid state: Timed out
        aggregate.markAuthenticated(true);
        aggregate.markActive(true);
        // Set last activity to 31 minutes ago (assuming 30 min timeout in aggregate)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
        aggregate.setStateChecksum("valid-checksum");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "session-bad-state";
        aggregate = new TellerSessionAggregate(sessionId);

        // Setup invalid state: Null checksum
        aggregate.markAuthenticated(true);
        aggregate.markActive(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setStateChecksum(null); // Violation
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(aggregate.id());
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We check for IllegalStateException which maps to Domain Error in this bounded context
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
