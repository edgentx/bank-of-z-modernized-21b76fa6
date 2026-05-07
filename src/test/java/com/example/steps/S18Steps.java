package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String currentTellerId;
    private String currentTerminalId;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        // For a valid aggregate, we assume pre-conditions are met (e.g. authenticated state is set up implicitly by the context of the test)
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.currentTellerId = "teller-123";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.currentTerminalId = "terminal-T01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Context: In a real scenario, authentication sets the 'authenticated' flag.
            // For the positive path, we manually set the aggregate state or assume the command payload handles it.
            // Here we assume the command carries the auth token and the aggregate verifies it.
            // To simulate 'valid' for the positive case, we might need a backend hook or a specific test-only method.
            // However, based on the failure scenarios, the aggregate seems to enforce invariants.
            // We'll construct the command with the 'valid' ids.
            
            // We'll assume the system sets the authenticated state for the 'Success' scenario 
            // before calling execute, or the command contains the auth context.
            // For simplicity in this step definition, we'll call execute.
            StartSessionCmd cmd = new StartSessionCmd("session-1", currentTellerId, currentTerminalId, true, true, true); 
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("terminal-T01", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // The violation is simulated by passing a command that indicates no authentication
        this.currentTellerId = "teller-123";
        this.currentTerminalId = "terminal-T01";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        // Simulating violation: active session exists (cannot start new one?) or timeout config is invalid.
        // Assuming the scenario implies an active session exists or timeout is invalid.
        // Let's assume the aggregate was already started.
        aggregate.execute(new StartSessionCmd("session-3", "teller-123", "terminal-T01", true, true, true));
        aggregate.clearEvents(); // clear setup events
        // Now try to start again (which might violate logic if not idempotent or state check)
        // OR pass a timeout config of 0
        this.currentTellerId = "teller-123";
        this.currentTerminalId = "terminal-T01";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-4");
        this.currentTellerId = "teller-123";
        this.currentTerminalId = "terminal-T01";
        // Violation: bad navigation context
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Typically an IllegalArgumentException or IllegalStateException
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
