package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper constants
    private static final String SESSION_ID = "SESSION-123";
    private static final String TELLER_ID = "TELLER-Alice";
    private static final String TERMINAL_ID = "TERM-B1";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Ensure valid state
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(null); // Fresh session
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Valid ID is just a non-blank string, handled in When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Valid ID is just a non-blank string, handled in When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(null);
        // Auth failure is indicated by the command flag being false, handled in When step
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setNavigationState("IDLE");
        // Set last activity to recent (e.g. 1 minute ago), which is < 15 min timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(1)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Set navigation to something other than IDLE (e.g., stuck in a transaction screen)
        aggregate.setNavigationState("TRANSACTION_IN_PROGRESS");
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Construct command based on the scenario context (which 'Given' was called)
        // Default to valid params unless specific violation state implies invalid params
        // The violation for "Auth" is determined by the command flag.
        boolean isAuthenticated = !(aggregate.getCurrentNavigationState() != null 
                && aggregate.getCurrentNavigationState().equals("IDLE") 
                && aggregate.getLastActivityAt() == null 
                && !"IDLE".equals(aggregate.getCurrentNavigationState())); // Heuristic to detect the Auth Given

        // Check specific Givens more explicitly via internal state check
        // The Auth Violation scenario sets Nav to IDLE and time to null, same as Valid.
        // We need a way to differentiate. Let's check a flag or inspect the aggregate state more closely.
        // Simpler: We'll rely on the setup logic. 
        // If we are in the Auth violation scenario, the prompt implies the aggregate/auth context is invalid.
        // We simulate this by passing false to the command.
        
        boolean isAuthViolation = (aggregate.getCurrentNavigationState().equals("IDLE") && aggregate.getLastActivityAt() == null);
        // However, this is true for Valid Scenario too. Let's use a specific marker or strict sequence.
        // Actually, let's just interpret the scenarios:
        // 1. Valid -> Auth=true, Term=true, Nav=IDLE, Time=null
        // 2. Auth Violation -> Auth=false, Nav=IDLE, Time=null
        // 3. Timeout -> Auth=true, Nav=IDLE, Time=Now-1min
        // 4. Nav Violation -> Auth=true, Nav=TRANS, Time=null

        boolean cmdAuthenticated = true;
        if (isAuthViolation && aggregate.getCurrentNavigationState().equals("IDLE") && !aggregate.getCurrentNavigationState().equals("TRANSACTION_IN_PROGRESS")) {
             // This matches the Auth Violation setup logic if we can't distinguish.
             // To fix ambiguity, let's assume standard Valid is default.
             // We will derive the command flags based on checking the aggregate state.
        }
        
        // Refined Logic:
        cmdAuthenticated = true; // Default
        if (aggregate.getCurrentNavigationState().equals("IDLE") && aggregate.getLastActivityAt() == null) {
             // Ambiguous: Could be Valid or Auth Violation. 
             // We'll use a thread-local or specific check? No, let's look at the violations.
             // Actually, the Auth violation setup is: aggregate.setNavigationState("IDLE"); setLastActivity(null);
             // The Valid setup is: aggregate.setNavigationState("IDLE"); setLastActivity(null);
             // They are identical in setup. 
             // FIX: I will modify the Auth Violation setup in the Given method to set a marker, or assume the test runner isolates scenarios.
             // Assuming isolation: 
             // If we are in Auth violation, we pass false.
             // How to detect? We can't perfectly. 
             // However, the scenario "StartSessionCmd rejected — A teller must be authenticated..."
             // implies we simply need to run it with false. 
             // Since the Givens are distinct methods, I will use a specific flag in the steps class or rely on checking the specific violation condition.
             
             // Workaround: The "Valid" scenario has `And valid tellerId...`. The "Auth" one doesn't.
             // But the `And valid tellerId` Given just returns void.
             
             // Let's inspect the aggregate's last activity. If it is recent, it's the Timeout scenario.
             if (aggregate.getLastActivityAt() != null) {
                 // Timeout scenario, Auth is true
                 cmdAuthenticated = true;
             } else if (!"IDLE".equals(aggregate.getCurrentNavigationState())) {
                 // Nav violation, Auth is true
                 cmdAuthenticated = true;
             } else {
                 // It's IDLE and null timestamp. Is it Valid or Auth Violation?
                 // We will default to Valid. To make Auth Violation work, we need the test to drive the false flag.
                 // I will add a specific check or a helper field.
                 // OR: The "Auth Violation" scenario is the only one that expects a failure on a clean slate.
                 // Let's rely on a context variable.
             }
        }
        
        // Correct Approach: Use a context variable to drive the command params.
        // However, standard Cucumber isolates scenarios. We just need the logic inside When to match the Given.
        // Let's assume the aggregate state dictates the params.
        // The only difference between Valid and AuthViolation Given is... nothing in the current implementation.
        // I will update the AuthViolation Given to set a specific marker or simply assume the failure logic matches the condition.
        
        // Let's try a different logic:
        // We assume the command parameters are provided externally or we calculate them.
        // If `isAuthenticated` is NOT explicitly set to false by the scenario, we assume true.
        // How do we know it's the Auth scenario?
        // I will assume that the "Auth Violation" scenario is the one where we want to test auth failure.
        // To ensure deterministic behavior, I'll assume the Valid scenario runs with Auth=true.
        // The Auth violation scenario: The prompt says "Given a TellerSession aggregate that violates: A teller must be authenticated..."
        // This implies the *state* of the world violates the rule. In DDD, the *command* validation checks the rule.
        // So we pass isAuthenticated=false to the command to simulate the security context saying "No".
        
        // Strategy: check a flag set by the Given.
        if (this.authViolationScenarioDetected) {
            cmdAuthenticated = false;
        }

        Command cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID, cmdAuthenticated, true);

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    private boolean authViolationScenarioDetected = false;
    
    // Hook to detect which scenario is running (Simple approach)
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void markAuthViolation() {
        a_teller_session_aggregate_that_violates_authentication();
        authViolationScenarioDetected = true;
    }
    
    // Reset flag for other scenarios if needed, though Cucumber re-instantiates steps class per scenario.

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(SessionStartedEvent.class, resultEvents.get(0).getClass());
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(TELLER_ID, event.tellerId());
        assertEquals(TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect IllegalStateException or IllegalArgumentException based on our implementation
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_a_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setNavigationState("IDLE");
        aggregate.setLastActivityAt(null);
    }
}
