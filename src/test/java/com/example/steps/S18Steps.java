package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Ensure it starts in a clean state (authentication check in aggregate relies on specific ID or flag)
        // For 'valid' path, we mark it as authenticated internally or via ID convention
        this.aggregate.markAsAuthenticated();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Valid ID is handled in the When step construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Valid ID is handled in the When step construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Construct valid command
        this.cmd = new StartSessionCmd("session-123", "AUTHENTICATED_USER", "TERM-01");
        executeCommand();
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("AUTHENTICATED_USER", event.tellerId());
        assertEquals("TERM-01", event.terminalId());
        assertEquals("session.started", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Ensure NOT authenticated
        // The command will use a non-auth ID or the aggregate flag is false.
        // We won't call markAsAuthenticated().
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAsAuthenticated();
        
        // Simulate an existing session that is old
        this.aggregate.setStatus(TellerSessionAggregate.Status.STARTED);
        this.aggregate.setLastActivity(Instant.now().minus(Duration.ofHours(1))); // 1 hour ago > 30 min timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAsAuthenticated();
        // Set navigation to something invalid for starting a session (e.g. deep in a transaction)
        this.aggregate.setNavigationState("CASH_DEPOSIT_SCREEN_2");
    }

    @When("the StartSessionCmd command is executed on invalid state")
    public void the_start_session_cmd_command_is_executed_on_invalid_state() {
        // Construct command. Auth ID check depends on aggregate state setup.
        this.cmd = new StartSessionCmd("session-123", "UNAUTHENTICATED_USER", "TERM-01");
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
    }

    private void executeCommand() {
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}