package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-001");
        // Assume valid defaults
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in context of command execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in context of command execution
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-AUTH");
        // Force state to a condition where session cannot start
        // or simulate auth failure via command flags
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-TIMEOUT");
        // Use a command with an expiration time in the past to simulate timeout logic
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-ERR-NAV");
        // Simulate invalid context via command flags
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        caughtException = null;
        try {
            // We derive the specific command flavor based on the setup above
            Command cmd = createCommandForCurrentContext();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        assertEquals("session.started", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // In this domain logic pattern, errors are often RuntimeExceptions
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException ||
                   caughtException instanceof UnknownCommandException);
    }

    // Helper to route context to the specific command variant used in testing
    private Command createCommandForCurrentContext() {
        String id = aggregate.id();
        
        // Default valid command
        if (id.equals("SESSION-001")) {
            return new StartSessionCmd(id, "TELLER-101", "TERM-202", Instant.now().plus(Duration.ofHours(1)), "DEFAULT");
        }
        // Auth violation (Unauthenticated flag)
        if (id.equals("SESSION-ERR-AUTH")) {
            return new StartSessionCmd(id, "TELLER-101", "TERM-202", Instant.now().plus(Duration.ofHours(1)), "UNAUTHENTICATED");
        }
        // Timeout violation (Expired time)
        if (id.equals("SESSION-ERR-TIMEOUT")) {
            return new StartSessionCmd(id, "TELLER-101", "TERM-202", Instant.now().minus(Duration.ofSeconds(1)), "DEFAULT");
        }
        // Navigation violation (Invalid Context)
        if (id.equals("SESSION-ERR-NAV")) {
            return new StartSessionCmd(id, "TELLER-101", "TERM-202", Instant.now().plus(Duration.ofHours(1)), "INVALID_CTX");
        }
        throw new IllegalStateException("Unknown aggregate context for testing: " + id);
    }

    // Mock Repository for testing compliance
    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        // In-memory implementation placeholder
        @Override
        public TellerSessionAggregate load(String id) { return null; }
        @Override
        public void save(TellerSessionAggregate aggregate) {}
    }
}