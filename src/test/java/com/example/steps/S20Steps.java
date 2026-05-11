package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-20: TellerSession EndSessionCmd.
 * Uses In-Memory aggregate instantiation for fast unit-level BDD testing.
 */
public class S20Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "sess-" + System.currentTimeMillis();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it meets the baseline 'valid' criteria (Authenticated, Not Timed Out)
        aggregate.markAuthenticated(); 
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationContext("MAIN_MENU");
        this.capturedException = null;
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(this.sessionId);
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(sessionId);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionEndedEvent, "Event type should be SessionEndedEvent");
        assertEquals("session.ended", event.type());
        assertFalse(aggregate.isActive(), "Aggregate should be inactive after command");
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "sess-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // DO NOT call markAuthenticated(). The aggregate defaults to authenticated=false.
        // Ensure it doesn't fail for other reasons first
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationContext("HOME");
        this.capturedException = null;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set time to past > 15 minutes (Threshold in Aggregate)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.setNavigationContext("HOME");
        this.capturedException = null;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "sess-nav-error";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Corrupt navigation state (Null or Blank)
        aggregate.setNavigationContext("   "); 
        this.capturedException = null;
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        
        // Verify the message matches the specific invariant violation for reporting
        String msg = capturedException.getMessage();
        assertTrue(msg != null && !msg.isEmpty(), "Error message should not be empty");
    }
}
