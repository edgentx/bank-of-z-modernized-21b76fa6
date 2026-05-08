package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("session-123");
        aggregate.markAuthenticated(); // Pre-condition for valid start in some contexts, but we handle init in cmd
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context handled in the 'When' clause construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context handled in the 'When' clause construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-123", "teller-42", "terminal-01");
            aggregate = new TellerSession("session-123");
            aggregate.markAuthenticated(); // Ensure valid state for success case
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
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.tellerId());
        assertEquals("terminal-01", event.terminalId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-401");
        // Intentionally NOT calling markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-timeout");
        aggregate.markAuthenticated();
        aggregate.markExpired(); // Simulate timeout violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.corruptNavigationState(); // Simulate nav state error
    }

    @When("the StartSessionCmd command is executed on invalid aggregate")
    public void the_start_session_cmd_command_is_executed_on_invalid_aggregate() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("session-error", "teller-1", "term-1");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Re-link generic 'When' for rejection scenarios to specific implementation
    // Cucumber doesn't support overload logic well, so we use a distinct method name above for error injection,
    // but the feature file uses the same line. We need a unified entry point or regex.
    // For this implementation, I will update the unified method to handle the 'violates' context.
    
    // Re-declaring the unified When to catch all cases based on state
    @When("the StartSessionCmd command is executed")
    public void unified_start_session_execution() {
        try {
            // Use IDs matching the violation setup if applicable, or defaults
            String id = "session-generic";
            if(aggregate != null) id = aggregate.id();
            
            StartSessionCmd cmd = new StartSessionCmd(id, "teller-1", "terminal-1");
            // The aggregate state was set up in the Given steps
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
