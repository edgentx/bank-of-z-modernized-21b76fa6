package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private record InMemoryTellerSessionRepository(TellerSessionAggregate aggregate) implements TellerSessionRepository {
        @Override public TellerSessionAggregate load(String id) { return aggregate; }
        @Override public void save(TellerSessionAggregate aggregate) { }
    }

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo;
    private Throwable thrownException;

    public S18Steps() {
        this.repo = new InMemoryTellerSessionRepository(null);
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Inject repo mock for any complex logic, though current impl is stateless
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Used contextually in the 'When' step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Used contextually in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate the aggregate being in a state where auth check would fail.
        // In this simple implementation, we pass an unauthenticated flag to the command or check internal state.
        // We'll handle this via the command payload or a setup flag in a real system.
        // For this exercise, we assume the command carries the necessary info.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "sess-" + UUID.randomUUID();
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Simulating positive flow for the first scenario, negative for others based on context
            boolean isAuthenticated = !this.aggregate.getClass().getSimpleName().contains("violates"); 
            // Note: The above is a heuristic for the test structure. 
            // Real test would use specific context flags.
            
            // We construct the command. The specific violation logic depends on how the Aggregate validates.
            // Let's assume valid data for the happy path.
            Command cmd = new StartSessionCmd(aggregate.id(), "teller-123", "term-456", true);
            
            // If the scenario implies a violation, we might need to trigger the internal state or command data differently.
            // For this stub, we execute and catch exceptions.
            List<DomainEvent> events = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(aggregate.uncommittedEvents(), "Events should not be null");
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have at least one event");
        Assertions.assertTrue(
            aggregate.uncommittedEvents().stream().anyMatch(e -> "session.started".equals(e.type())),
            "Expected session.started event"
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
