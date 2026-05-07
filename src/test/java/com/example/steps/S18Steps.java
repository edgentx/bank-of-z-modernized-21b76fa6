package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    // Test Doubles
    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private TellerSessionAggregate store;
        @Override public void save(TellerSessionAggregate aggregate) { this.store = aggregate; }
        @Override public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.ofNullable(store);
        }
    }

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    // Scenario Helpers
    private String sessionId = "TS-123";
    private String tellerId = "T-99";
    private String terminalId = "TERM-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Assume pre-populated or loaded via repo, but for unit test we instantiate
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "T-99";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "TERM-01";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Force the aggregate into a state where auth is false
        // The command execution should fail if the aggregate logic enforces auth on existing state
        // OR if the command requires it. The prompt says "aggregate violates".
        // Let's assume the aggregate tracks auth and StartSessionCmd enforces it.
        // However, StartSessionCmd *sets* auth. So we simulate a case where the aggregate 
        // is in a state that conflicts (e.g. strict policy).
        // Actually, usually StartSession IS the auth action. 
        // Let's interpret the violation as: The aggregate is already initialized but unauthenticated 
        // and the system policy forbids re-sessions without valid token.
        // Simpler: We create the command with a null/blank tellerId or tamper with the aggregate 
        // to fail internal checks.
        // Let's use the explicit violation method for the test context.
        aggregate.markUnauthenticated(); 
        // We will pass a valid command, but the aggregate logic might check a flag.
        // Note: Since StartSessionCmd *starts* it, usually it handles the transition to authenticated.
        // To enforce the "Rejected" scenario, we will pass invalid data in the command in the 'When' step 
        // OR use a specific aggregate state.
        // Let's stick to the prompt: "aggregate violates".
        // This implies the aggregate itself is in a bad state for the command.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markSessionTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markNavigationStateInvalid();
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Setup Command based on scenario context
            // If violation requires bad data, we could pass nulls here.
            // Based on "aggregate violates", we assume the aggregate state determines failure.
            this.command = new StartSessionCmd(sessionId, tellerId, terminalId);
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Domain errors usually manifest as IllegalStateException or IllegalArgumentException within the execute block
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
        
        // Specific checks for scenario context
        if (aggregate.getSessionTimeoutAt() != null && aggregate.getSessionTimeoutAt().isBefore(Instant.now())) {
            assertTrue(caughtException.getMessage().contains("timeout") || caughtException.getMessage().contains("inactive"));
        }
    }
}
