package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd on TellerSession.
 */
public class S18Steps {

    // Context variables
    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-01";
    private String validNavigationState = "HOME_SCREEN";
    
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid pre-condition for most tests unless overridden
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Using default tellerId
        assertNotNull(tellerId);
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Using default terminalId
        assertNotNull(terminalId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Default constructor ensures unauthenticated state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markExpired(); // Force the timestamp to be old
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // We will violate this by passing a null/blank state in the command in the 'When' step or manipulate aggregate state if needed
        // However, the logic validates the command payload. 
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        caughtException = null;
        try {
            // For the navigation state violation scenario, we pass a blank string
            String navState = (aggregate.isAuthenticated() && !aggregate.isActive() && aggregate.getLastActivityAt() != null && aggregate.getLastActivityAt().isBefore(java.time.Instant.now().minusSeconds(1))) 
                             ? "HOME_SCREEN" 
                             : "HOME_SCREEN";
            
            // Specific handling for the violation scenario where nav state is bad
            if (aggregate.isAuthenticated() && aggregate.getLastActivityAt() == null) {
                 // Normal case
            }
            
            // Actually, to test the violation "Navigation state...", we need to pass a bad nav state
            // Let's inspect the aggregate state to decide
            // Since the 'Given' for Navigation violation doesn't set a specific flag, we'll rely on the step order or a flag.
            // Simplified: I will use a logic check. If the aggregate is unauthenticated and we are in the 'Invalid Context' scenario (which doesn't set auth), it's covered.
            // But the violation specifically says "Navigation state...". 
            // Let's assume the command needs the bad state.
            
            String navToUse = validNavigationState;
            // Hack to identify the specific violation scenario for Navigation State
            // We can check if the aggregate is authenticated but not marked expired (differentiating from auth/expired scenarios)
            if (aggregate.isAuthenticated() && !aggregate.isActive()) {
               // This is the generic valid setup
            } else if (!aggregate.isAuthenticated()) {
               // Auth violation scenario
            } else if (aggregate.getLastActivityAt() != null && aggregate.getLastActivityAt().isBefore(java.time.Instant.now().minusSeconds(3600))) {
               // Timeout violation scenario
            }
            
            // The Scenario "Navigation state must accurately reflect..." 
            // In the Given step I didn't set a specific flag. Let's assume if it's Auth=true, Active=false, Expired=not set
            // To strictly test the Navigation state validation in the command, we need to pass a bad value.
            // The Gherkin doesn't pass data to When. So the step definition must decide what data to pass.
            // I will create a command with invalid state for that specific scenario.
            
            if (aggregate.isAuthenticated() && aggregate.getLastActivityAt() == null) {
                // This is the valid setup, use valid nav
                navToUse = validNavigationState;
            } else if (!aggregate.isAuthenticated()) {
                 // Auth test
                 navToUse = validNavigationState;
            } else if (aggregate.getLastActivityAt() != null) {
                 // Timeout test
                 navToUse = validNavigationState;
            } else {
                 // This path is hit if we manually called a 'markInvalidNavigation' setup which we didn't implement strongly.
                 // Let's look at the 'Given' for Navigation violation again. It just creates an aggregate.
                 // I will differentiate by instance variables or a simple heuristic.
                 // Heuristic: The Navigation Given creates a new aggregate, marks Auth (implicitly required to reach that check), 
                 // but I'll override the command param.
            }

            // Refining the logic to map Scenarios to Command Data
            // 1. Valid: Auth=true. Cmd(Terminal=OK, Nav=OK).
            // 2. Auth Violation: Auth=false. Cmd(Terminal=OK, Nav=OK). -> Fails on Auth check.
            // 3. Timeout Violation: Auth=true, Expired=true. Cmd(Terminal=OK, Nav=OK). -> Fails on Timeout check.
            // 4. Nav Violation: Auth=true, ValidState=true. Cmd(Terminal=OK, Nav=NULL). -> Fails on Nav check.
            
            // Since the 'Given' for Navigation violation doesn't pass a distinct flag, I'll assume it's the one 
            // where we construct the aggregate but explicitly prepare to send a bad command.
            // However, to keep steps simple, I will assume the standard command is used unless the context implies otherwise.
            // For the purpose of this exercise, I will construct the command normally. 
            // If the test for "Navigation state" fails, it implies I need to send a bad command.
            // I'll assume the "Navigation violation" scenario requires me to send a command with blank nav.
            
            // How to detect we are in that scenario? 
            // The Given method `a_teller_session_aggregate_that_violates_navigation_state` creates the aggregate.
            // I can check a flag or field on the aggregate.
            // Let's assume I set a field `navigationState` to null in the aggregate in the Given.
            // But the command carries the state.
            // I will pass `null` as nav state if the aggregate's internal state (which we usually copy) is suspicious.
            
            // Actually, I'll just use a conditional based on the scenario title context inferred from setup.
            // S-18.feature context is not available here easily. 
            // I will modify the `a_teller_session_aggregate_that_violates_navigation_state` to set a marker, e.g. `aggregate.markInvalidNavigationState()`.
            // I already added that void method to the aggregate. It doesn't change state, but let's make it return a boolean or set a private field.
            // Wait, I can't change the aggregate structure that easily without updating the model class.
            // The model class `markInvalidNavigationState` does nothing currently.
            // I will simply check `if (aggregate.isAuthenticated() && aggregate.getLastActivityAt() == null && aggregate.isActive() == false)`. 
            // Wait, that matches the Valid case too.
            
            // Let's make it simple: I will use a thread-local or a simple field `currentScenario` set in the Given steps.
            // Or better: `if (aggregate.getClass().getName().contains("Mock"))` - No.
            
            // Let's assume the "Navigation State" scenario requires me to pass `null` in the command.
            // I will hardcode the check: if the aggregate is NOT marked expired and IS authenticated, I'll check if I should fail nav.
            // I'll just default to Valid command. If Cucumber runs the scenario, it will fail the assertion if my logic is wrong.
            // To ensure the "Navigation" scenario works, I must pass `null` for nav state in that specific case.
            // I will infer this by checking if the aggregate was created via the specific `Given`.
            // Since I can't, I will rely on the fact that the `a_teller_session_aggregate_that_violates_navigation_state` step sets a specific distinguishable state or I'll just assume `validNavigationState` is used and the test might fail if I don't implement the violation logic.
            // But I want it to pass.
            // I will add a `scenario` field to this Step class and set it in the Given methods.
        } catch (Exception e) {
            // Command execution logic
        }

        // Re-implementing execution cleanly
        StartSessionCmd cmd;
        
        // Infer command data based on state setup in Given steps
        // The violation for Navigation State is specific.
        // Let's assume `validNavigationState` is used unless we are in the violation case.
        // I'll detect the violation case by checking `if (aggregate.isAuthenticated() && !aggregate.isActive() && aggregate.getLastActivityAt() == null)`. 
        // This matches the Valid setup exactly. I need a discriminator.
        // I'll modify `a_teller_session_aggregate_that_violates_navigation_state` to set a specific (mock) field or I'll just accept that the 'Valid' scenario passes and 'Navigation' scenario requires a specific call.
        // I'll check if `aggregate` is an instance of a special mock? No.
        // I will use the simple trick: checking `hashCode` or `System.identityHashCode`? No.
        // I'll just use a String field `scenarioType`.
    }

    // Clean implementation of state handling for clarity
    private String scenarioType = "VALID";

    @Given("a valid TellerSession aggregate")
    public void setup_valid() {
        scenarioType = "VALID";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
    }
    
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setup_auth_violation() {
        scenarioType = "AUTH_VIOLATION";
        aggregate = new TellerSessionAggregate(sessionId);
        // default is not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setup_timeout_violation() {
        scenarioType = "TIMEOUT_VIOLATION";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markExpired();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setup_nav_violation() {
        scenarioType = "NAV_VIOLATION";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // We need to trigger the validation in the command. 
        // We'll pass null/blank nav state in the command.
    }

    @When("the StartSessionCmd command is executed")
    public void execute_start_session_cmd() {
        caughtException = null;
        try {
            String nav = (scenarioType.equals("NAV_VIOLATION")) ? "" : validNavigationState;
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, nav);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
