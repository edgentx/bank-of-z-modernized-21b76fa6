package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "session-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(id);
        // Setup valid state defaults (Auth=true, Context=valid, Time=Now)
        aggregate.markAuthenticated(true);
        aggregate.setOperationalContextValid(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Command construction happens in the When step or stored here
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Command construction happens in the When step or stored here
    }

    // --- Violation Givens ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String id = "session-violation-auth";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(false); // The violation
        aggregate.setOperationalContextValid(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String id = "session-violation-timeout";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(true);
        aggregate.setOperationalContextValid(true);
        // Set time to 31 minutes ago (Assuming 30 min timeout)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(31 * 60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        String id = "session-violation-context";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(true);
        aggregate.setOperationalContextValid(false); // The violation
        aggregate.setLastActivityAt(Instant.now());
    }

    // --- Whens ---

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Create command with valid data structure. The violations are in the Aggregate state set above.
        // If aggregate ID is null (implied by some Gherkin interpretations), we handle it, but here we have an instance.
        String sid = (aggregate != null) ? aggregate.id() : "unknown";
        command = new StartSessionCmd(sid, "teller-123", "terminal-T01");

        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events, but got null");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("terminal-T01", event.terminalId());
        
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown, but it wasn't");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Expected domain error (IllegalStateException or IllegalArgumentException), got: " + caughtException.getClass().getSimpleName());
        
        // We expect the event list NOT to be committed in case of error.
        // In our simple aggregate, execute throws before addEvent.
        assertNull(resultEvents, "Expected no events to be returned on error");
    }
}
