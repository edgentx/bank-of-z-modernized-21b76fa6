package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("TS-001");
        // Assuming we need to hydrate the aggregate to a valid state if necessary,
        // but for starting a session, the aggregate usually starts fresh or in a specific state.
        // Based on "Successfully execute", we assume a fresh valid instance.
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context stored in the command execution step
    }

    @Given("a valid terminalId is provided")
    public void a valid_terminal_id_is_provided() {
        // Context stored in the command execution step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default valid command for the positive scenario
        StartSessionCmd cmd = new StartSessionCmd("TS-001", "T-123", "TERM-A", true, 0, "HOME");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("TS-002");
        // The command will have authenticated = false
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_start_session_cmd_command_is_executed_invalid_auth() {
        StartSessionCmd cmd = new StartSessionCmd("TS-002", "T-123", "TERM-A", false, 0, "HOME");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("TS-003");
        // The command will have a timeout <= 0 (or some specific invalid value logic)
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_start_session_cmd_command_is_executed_invalid_timeout() {
        StartSessionCmd cmd = new StartSessionCmd("TS-003", "T-123", "TERM-A", true, -1, "HOME");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("TS-004");
        // The command will have a null/blank navigation context
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void the_start_session_cmd_command_is_executed_invalid_nav_state() {
        StartSessionCmd cmd = new StartSessionCmd("TS-004", "T-123", "TERM-A", true, 30, ""); // Blank nav state
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    // Specific When methods for violation scenarios to overload the generic one if needed, 
    // or we can use a single When if Cucumber allows parameterization. 
    // Given the specific text, I will map the violation scenarios to the specific When methods above.
    // However, the feature file text is identical: "When the StartSessionCmd command is executed".
    // Cucumber matches the first step definition it finds or requires unique text. 
    // To support the generic text in Gherkin, I will update the implementation to handle context switching 
    // or rely on the 'Given' setting the state for the generic 'When'.
    
    // Refactoring to a single When for all scenarios based on Gherkin text provided:
    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_generic() {
        // We assume the aggregate ID and specific command details are determined by the 'Given' steps.
        // For simplicity in this BDD setup, I'll re-map specific executions here or in the Gherkin if I could.
        // Since I must stick to the provided Gherkin text, I have to assume the context is implicit 
        // or I need to inspect the aggregate state (which is not clean).
        // Better approach: The specific violation steps above match the intent, but the Gherkin text is generic.
        // I will add specific Scenario names to the method names or use a single entry point.
        
        // Let's assume the positive flow for the first scenario.
        if (aggregate.getId().equals("TS-001")) {
             the_start_session_cmd_command_is_executed();
        } else if (aggregate.getId().equals("TS-002")) {
             the_start_session_cmd_command_is_executed_invalid_auth();
        } else if (aggregate.getId().equals("TS-003")) {
             the_start_session_cmd_command_is_executed_invalid_timeout();
        } else if (aggregate.getId().equals("TS-004")) {
             the_start_session_cmd_command_is_executed_invalid_nav_state();
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("TS-001", event.aggregateId());
        assertEquals("T-123", event.tellerId());
        assertEquals("TERM-A", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    // Inner class for the InMemory repository to keep it self-contained or assume it exists.
    // Based on instructions, I should assume standard patterns.
    public static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        // Simple in-memory map implementation for test purposes
        // Assuming TellerSessionRepository interface exists or is implied.
        // If not, I'll define the aggregate execution purely in-memory.
        // The steps above use the aggregate directly, so this is just a placeholder if needed.
        @Override
        public TellerSessionAggregate load(String id) {
            return null;
        }
        @Override
        public void save(TellerSessionAggregate aggregate) {
        }
    }
}
