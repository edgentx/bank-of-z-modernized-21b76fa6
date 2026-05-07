package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // TellerId will be set in the command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // TerminalId will be set in the command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default command construction for happy path
        if (command == null) {
            command = new StartSessionCmd("session-123", "teller-001", "term-ABC", true);
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-001", event.tellerId());
        assertEquals("term-ABC", event.terminalId());
        assertEquals("session.started", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Create a command indicating the teller is NOT authenticated
        command = new StartSessionCmd("session-auth-fail", "teller-001", "term-ABC", false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Set last activity to 20 minutes ago (timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(20 * 60));
        
        // Command itself is valid, but aggregate state is invalid
        command = new StartSessionCmd("session-timeout", "teller-001", "term-ABC", true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Simulate an existing active session in a complex state
        // We can't manually set 'active' easily without reflection, so we rely on the logic
        // that an existing active session in a bad state blocks the new start.
        // However, startSession sets active=true. To test the invariant checking the *current* state,
        // we need to simulate a state where the aggregate thinks it is already in progress.
        // Since this is a fresh aggregate, we simulate this by setting the navigation state flag
        // to something unexpected for a fresh start, assuming the aggregate was loaded from repo.
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");
        
        command = new StartSessionCmd("session-nav-fail", "teller-001", "term-ABC", true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect IllegalStateException for invariant violations
        assertTrue(capturedException instanceof IllegalStateException);
    }
}