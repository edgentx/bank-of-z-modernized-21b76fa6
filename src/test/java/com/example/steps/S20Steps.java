package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Constants matching the Aggregate's internal config
    private static final Duration SESSION_TIMEOUT = Duration.of(15, ChronoUnit.MINUTES);

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup a valid, active session
        aggregate = new TellerSessionAggregate("sess-123");
        aggregate.activate(
            "teller-1", 
            true, // authenticated
            Instant.now(), // active
            "HOME" // valid context
        );
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId is implicitly handled by the aggregate initialization above
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("sess-auth-fail");
        // Active but NOT authenticated
        aggregate.activate(
            "teller-1",
            false, // NOT authenticated
            Instant.now(),
            "HOME"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("sess-timeout");
        // Authenticated, but activity was long ago
        Instant pastActivity = Instant.now().minus(SESSION_TIMEOUT).minus(1, ChronoUnit.MINUTES);
        aggregate.activate(
            "teller-1",
            true,
            pastActivity, // STALE
            "HOME"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("sess-nav-error");
        // Authenticated, Active, but stuck in a deep transaction state
        aggregate.activate(
            "teller-1",
            true,
            Instant.now(),
            "CASH_WITHDRAWAL_CONFIRM" // Invalid context for termination
        );
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id(), "teller-1");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent);
        
        SessionEndedEvent endedEvent = (SessionEndedEvent) event;
        assertEquals("session.ended", endedEvent.type());
        assertEquals("sess-123", endedEvent.aggregateId());
        assertFalse(aggregate.isActive(), "Session should be inactive");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
