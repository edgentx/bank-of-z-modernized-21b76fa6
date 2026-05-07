package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Default valid data
    private static final String VALID_SESSION_ID = "session-123";
    private static final String VALID_TELLER_ID = "teller-01";
    private static final String VALID_TERMINAL_ID = "term-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Ensure valid state for the happy path defaults
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationValid(true);
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Implicitly handled in the 'When' step construction of the command
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Implicitly handled in the 'When' step construction of the command
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        assertEquals("session.started", event.type());
        assertEquals(VALID_SESSION_ID, event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(false); // The violation
        aggregate.setNavigationValid(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setNavigationValid(true);
        // Set last activity to 31 minutes ago (violating the 30 min timeout in aggregate)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setNavigationValid(false); // The violation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }

}