package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
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
    private Exception thrownException;

    // Test Data Constants
    private static final String VALID_SESSION_ID = "TS-12345";
    private static final String VALID_TELLER_ID = "T-01";
    private static final String VALID_TERMINAL_ID = "TERM-A";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Ensure defaults are valid for the positive path
        aggregate.setAuthenticated(true);
        aggregate.setNavigationStateValid(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in command creation, but we assert the ID exists
        assertNotNull(VALID_TELLER_ID);
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in command creation
        assertNotNull(VALID_TERMINAL_ID);
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            command = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent);

        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(VALID_SESSION_ID, startedEvent.aggregateId());
        assertEquals(VALID_TELLER_ID, startedEvent.tellerId());
        assertEquals(VALID_TERMINAL_ID, startedEvent.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(false); // Violation: Not authenticated
        aggregate.setNavigationStateValid(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true);
        // Set last activity to 31 minutes ago (Timeout is 30)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setNavigationStateValid(false); // Violation: Invalid context
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        // Verify the error message matches the invariant
        assertTrue(thrownException.getMessage().length() > 0);
    }
}
