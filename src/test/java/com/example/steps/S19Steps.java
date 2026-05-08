package com.example.steps;

import com.example.domain.aggregator.model.*;
import com.example.domain.aggregator.repository.*;
import com.example.domain.shared.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.function.Executable;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to reset context
    private void resetAggregate(String sessionId) {
        this.aggregate = repository.loadOrCreate(sessionId);
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "sess-123";
        // Initialize a valid session state (authenticated, active)
        TellerSessionAggregate agg = new TellerSessionAggregate(sessionId);
        // We manually apply state changes to simulate 'valid' creation for test purposes
        // or rely on the aggregate starting clean and the command initializing it.
        // Based on requirements, a valid aggregate implies it's ready to accept commands.
        // We will assume the aggregate instance is valid by default for the happy path.
        this.aggregate = agg;
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the aggregate ID in the setup
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // This will be used in the 'When' step
        // No-op here, just ensuring the context is ready
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // This will be used in the 'When' step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        // We assume valid data for the happy path based on previous Givens
        executeCommand("sess-123", "MAIN_MENU", "ENTER");
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.targetMenuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "sess-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally not calling any authentication logic or setting auth state to false
        // Assuming default state or explicit invalid state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a timed out session
        this.aggregate.markAsExpired(); // Custom method for testing invariants
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        String sessionId = "sess-bad-ctx";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate invalid context state
        this.aggregate.invalidateContext();
    }

    @When("the NavigateMenuCmd command is executed on invalid aggregate")
    public void the_navigate_menu_cmd_command_is_executed_on_invalid_aggregate() {
        // Attempt to navigate from an invalid state
        executeCommand(aggregate.id(), "DEPOSITS", "SELECT");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect an IllegalStateException, IllegalArgumentException, or a specific DomainError
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException);
    }

    // --- Utilities ---

    private void executeCommand(String sessionId, String menuId, String action) {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action, Instant.now());
            resultEvents = aggregate.execute(cmd);
            // Apply events to update state if we were testing state persistence, 
            // though here we mostly check return values or errors.
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Inner class for test repository if not existing
    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate load(String id) {
            return new TellerSessionAggregate(id);
        }
        @Override
        public TellerSessionAggregate loadOrCreate(String id) {
            return new TellerSessionAggregate(id);
        }
        @Override
        public void save(TellerSessionAggregate aggregate) { }
    }
}