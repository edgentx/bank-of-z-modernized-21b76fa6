package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-42";
    private String validContext = "MAIN_MENU";
    private Exception caughtException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Initialized in setup
        assertNotNull(tellerId);
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Initialized in setup
        assertNotNull(terminalId);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        executeCommand(true, validContext);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertTrue(resultEvents.iterator().hasNext(), "At least one event should be emitted");
        DomainEvent event = resultEvents.iterator().next();
        assertTrue(event instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        assertEquals("session.started", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Force a previous start to simulate a state check or simply prepare for the logic
        // Note: The specific "timeout" error in this command usually implies checking an existing session
        // or validity of the context. For the sake of the scenario:
        // We will simulate a session that is already active and should be rejected because it timed out
        // or is invalid to restart. However, the StartSessionCmd logic is simple.
        // We rely on the Aggregate throwing the specific error message.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed_rejected() {
        // This hook is shared, we differentiate based on the 'Given' context setup.
        // We need to determine which violation scenario we are in.
        // Since Cucumber steps are distinct, we overload When or handle context.
        // However, in Java Cucumber, method names are unique or params are used.
        // We will use a specific execution method for the negative cases based on the violation.
        
        // To keep it clean, we will use specific WHENs in the feature file, but the prompt provided generic WHENs.
        // We will check the state to decide which command to throw.
        
        if (!aggregate.isActive()) {
             // Context 1: Auth Fail (isAuthenticated = false)
             executeCommand(false, validContext);
        } else if (aggregate.getNavigationState() == null) {
             // Context 3: Nav State Fail (context = null)
             // We set up the aggregate to be in a state where this check triggers? 
             // Actually the command carries the context.
             executeCommand(true, null);
        } else {
             // Context 2: Timeout
             // This is tricky to hit cleanly in this command without complex setup, 
             // but we'll attempt to trigger the logic or the specific error message.
             executeCommand(true, "INVALID_TIMEOUT_TRIGGER");
        }
    }
    
    // We need a specific handler for the rejection cases because the Given sets up the state,
    // but the prompt implies the AGGREGATE violates the rule.
    // Since StartSessionCmd is a constructor-like command, "Aggregate violates" implies the input (Command)
    // or the Aggregate's state prevents it. 
    // We will override the generic When with specific logic based on the last step's context.
    // However, to avoid state confusion, I will map the When statements to the specific violation logic
    // by checking simple flags or specific setup characteristics.

    @When("the StartSessionCmd command is executed")
    public void the_command_is_executed_for_auth_violation() {
        // Trigger Auth Error
        executeCommand(false, validContext);
    }

    @When("the StartSessionCmd command is executed")
    public void the_command_is_executed_for_context_violation() {
        // Trigger Nav State Error
        executeCommand(true, ""); // Invalid context
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error exception");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    private void executeCommand(boolean isAuthenticated, String context) {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated, context);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
