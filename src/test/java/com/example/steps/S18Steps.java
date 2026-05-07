package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // "AUTHENTICATED" simulates the teller ID that passes the auth check invariant
        this.tellerId = "AUTHENTICATED";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Setting tellerId to something that fails the internal check
        this.tellerId = "UNAUTHENTICATED_USER";
        this.terminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        this.tellerId = "AUTHENTICATED";
        this.terminalId = "TERM-01";
        
        // Manually set state to simulate an active session that has timed out
        aggregate.setActive(true);
        // Set last activity to 31 minutes ago (timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        this.tellerId = "AUTHENTICATED";
        this.terminalId = "TERM-01";
        aggregate.setInValidNavigationState(false);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        
        // Verify the message matches the invariant text from the story
        String message = caughtException.getMessage();
        assertTrue(
            message.contains("authenticated") || 
            message.contains("timeout") || 
            message.contains("Navigation state")
        );
    }
}
