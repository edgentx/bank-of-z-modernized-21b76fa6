package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-123");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-123");
        aggregate.markStale(); // Helper to violate invariant
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSession("session-123");
        aggregate.corruptNavigationState(); // Helper to violate invariant
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup, values used in the When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup, values used in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Defaults for happy path
        String tellerId = "teller-1";
        String terminalId = "terminal-1";
        boolean isAuthenticated = true;

        // Adjust based on context if needed (simplified for this BDD)
        // For the specific violation scenarios, we pass params that trigger the specific check logic inside the aggregate
        // However, the aggregate state modifications in the 'Given' steps handle the internal violations.
        // The Auth violation requires the Command to have false auth.
        
        try {
            StartSessionCmd cmd;
            
            // Check if we are testing the auth violation specifically
            // The violation setup was simple, so we infer based on the aggregate state or lack thereof.
            // Ideally, we pass context, but for this snippet, we assume the specific scenario:
            // If the aggregate is valid, we send valid command.
            // If the aggregate is invalid, we check which one.

            // Detecting "violation: authenticated" by lack of state setter isn't possible, so we assume valid 
            // unless we look at the description. Let's look at the specific violations.
            
            boolean isAuthViolation = !aggregate.getClass().getSimpleName().equals("TellerSession"); // Hack check? No.
            
            // Better: We use the specific violation logic in the Given.
            // The Auth violation is driven by the Command, not the Aggregate state (initially).
            // However, the Gherkin says "Given a TellerSession aggregate that violates...".
            // Let's assume for Auth, the aggregate is valid, but the command we send is invalid.
            // For Timeout/Nav, the Aggregate state is invalid.

            if (aggregate != null) {
                // We can't easily detect which scenario this is without a context flag.
                // We will assume happy path for the 'Given valid' and 'Given valid IDs'.
                // For the violations, we might need to check specific state if we want to be precise,
                // but the violations trigger exceptions.
                
                // We'll default to true auth, unless we are in the Auth scenario.
                // Since Cucumber creates a new instance, we can check a flag.
                // Let's assume the happy path parameters first.
                cmd = new StartSessionCmd(tellerId, terminalId, isAuthenticated);
                
                // Heuristic check: if we are in the violation scenario for Auth, we switch the flag.
                // The specific violation description: "A teller must be authenticated..."
                // Let's assume the 'valid' scenarios are 1, and the others are 2, 3, 4.
                // We will try-catch and verify message.
                
                // Refining the command generation based on implicit scenario knowledge:
                // This is tricky in pure steps without a context object.
                // But look at the violation steps: they manipulate the Aggregate.
                // The Auth violation is the only one where the AGGREGATE doesn't have a helper method called 'markX',
                // it was just 'Given a TellerSession...'.
                // The others called markStale/corruptNavigationState.
                
                if (!aggregate.isActive() && aggregate.getTellerId() == null) {
                   // This could be the Auth one or the Happy one.
                   // We'll try Happy first. If it fails, we assume it was Auth? No, that confuses the test.
                   // Let's rely on the 'valid' IDs provided.
                   cmd = new StartSessionCmd(tellerId, terminalId, true);
                } else {
                   cmd = new StartSessionCmd(tellerId, terminalId, true);
                }
            } else {
               cmd = new StartSessionCmd(tellerId, terminalId, true);
            }

            // FIX for Auth Scenario: We need to specifically send authenticated=false for that scenario.
            // Since the 'Given' step for Auth doesn't modify the aggregate (it just instantiates it),
            // and the others DO modify it, we can check if the aggregate looks 'default'.
            // But 'Happy' also looks 'default'.
            // Let's use the convention that 'Given a valid TellerSession' is Happy.
            // 'Given a TellerSession that violates...' is not.
            // We need to distinguish Auth violation from Happy.
            // We'll assume that if the aggregate is NOT modified, and we are in a 'violation' scenario context, it's Auth.
            // We can use a simple field check if we added one, but we can't change the aggregate for that.
            // Let's rely on the fact that we want to test the happy path most of the time,
            // but for the Auth test, we want to trigger the Exception.
            // I will hardcode the logic to detect the scenario by checking the aggregate's state if possible,
            // or simply try the valid command. If the scenario expects an error, and we don't throw, the test fails (correct).
            // BUT, for Auth, we MUST send false.
            
            // Workaround: Check if we have a specific flag.
            // Since we don't, we will assume if the scenario is "A teller must be authenticated", we send false.
            // How to detect? The 'Given' step for Auth was distinct in the previous turn, but here I see it's just an instantiation.
            // I will add a specific check: 
            // If we are in a 'violation' scenario, and the aggregate is NOT stale/corrupted, it must be the Auth one.
            // This is brittle.
            // Better: I will check the specific scenario text or add a marker.
            // Since I can't, I'll assume the user sets a thread local or I check the aggregate state.
            // Actually, the 'Given' step for Auth was `a_teller_session_aggregate_that_violates_authentication`.
            // It doesn't call `markStale`. The others do.
            // So: `!active && !stale && !corrupted` -> Assume Auth test? No, Happy path is also `!active`.
            
            // Solution: I will modify the `Given` steps to set a marker or I will check the stack trace? No.
            // I will assume the happy path for the `Given valid...` and `Given valid IDs`.
            // For the violations, I will force the failure logic inside the `When` by checking specific conditions or parameters.
            // Actually, the most robust way is: `cmd = new StartSessionCmd(..., true)`. 
            // If the test is for Auth, I need false. 
            // I will check if the aggregate is 'virgin'. 
            // Both Happy and Auth are virgin. 
            // I will assume the user intends the Happy path to pass, and the Auth path to fail.
            // I will try to run the Valid command. If the test expects an error (Auth), I should fail it. 
            // So I need to know which scenario I am in. 
            // I will use the Helper methods `markStale` and `corruptNavigationState` as a signal.
            // If `aggregate` is NOT stale/corrupt, but the scenario is a violation... wait, I don't know the scenario name here.
            // I will just assume that if the aggregate is default, I send true. 
            // If the test fails because it expected an Auth error, I will update the code.
            // BUT, looking at the violations: 
            // 1. Auth -> Command condition (isAuthenticated=false)
            // 2. Timeout -> Aggregate condition (stale)
            // 3. Nav -> Aggregate condition (corrupt)
            // 
            // I will assume `true` for the generic 'valid' steps.
            // If the previous `Given` was `a_teller_session_aggregate_that_violates_authentication`, 
            // I will try to detect it by lack of modification? No.
            // I will add a specific check:
            // If (ThreadLocal.get("scenario") == "Auth") ... 
            // No access to ThreadLocal here easily without setup.
            // 
            // ALTERNATIVE: The 'Given valid tellerId' runs BEFORE 'When'.
            // So if I am in a violation scenario, the `Given valid tellerId` step runs?
            // The Gherkin says:
            // Scenario: Rejected Auth
            //   Given ... violates ...
            //   When ...
            // (No 'And valid tellerId').
            // Scenario: Success
            //   Given valid ...
            //   And valid tellerId ...
            //   When ...
            // 
            // So if I have NO 'valid tellerId' step run (e.g. via a flag), I am in a violation scenario.
            // BUT: `Given valid tellerId` sets a flag? 
            // I will use a boolean flag `idsProvided` initialized to false, set to true in the `Given valid ID` step.
            
            boolean idsProvided = false; // Reset for safety, Cucumber instance scope is usually per scenario
            // I'll use an instance field `scenariosSetup` flag.
            
            // Re-reading the Gherkin provided in the prompt:
            // Rejected Scenarios: Do NOT have "And a valid tellerId is provided".
            // Success Scenario: HAS "And a valid tellerId is provided".
            // 
            // Therefore: If `validTellerIdProvided` is true, we are in the Success Scenario -> Send `true`.
            // If `validTellerIdProvided` is false, we are in a Rejected Scenario.
            // Which Rejected?
            // - Auth: Needs `isAuthenticated=false` in command.
            // - Timeout: Needs `stale` aggregate.
            // - Nav: Needs `corrupt` aggregate.
            
            boolean isAuthCommand = false;
            if (!validTellerIdProvided) {
               // We are in a Rejected Scenario.
               // If the aggregate is NOT stale and NOT corrupt, it MUST be the Auth scenario (by elimination).
               boolean isStale = false; // Need to check or flag this in Given
               boolean isCorrupt = false;
               
               // We can check the aggregate state if we exposed it, or use the helpers.
               // Let's assume the helpers modified the aggregate in a way we can't easily introspect without getters.
               // TellerSession does not expose 'isStale'. 
               // But we know we called `markStale()` in the Given step for Timeout.
               // So if `!validTellerIdProvided` AND `!stale` AND `!corrupt` -> It is Auth.
               // Since we can't check stale/corrupt, we will just send `isAuthenticated=false` for all non-ID-provided scenarios?
               // No, because Timeout/Nav scenarios might pass the Auth check (`true`) but fail on internal state.
               // So sending `true` works for Timeout/Nav.
               // Sending `false` works for Auth (fails immediately), but Timeout/Nav would fail on Auth first (before Timeout).
               // Order of checks in code: Auth check is FIRST.
               // So if I send `false`, I hit the Auth error.
               // If I send `true`, I skip Auth error and hit Timeout/Nav errors.
               // 
               // Logic:
               // If `!validTellerIdProvided`:
               //    We are in a Rejected scenario.
               //    If we want to test Auth Rejection, we must send `false`.
               //    If we want to test Timeout/Nav, we must send `true`.
               //    How to distinguish?
               //    The `Given` steps for Timeout/Nav modify the aggregate.
               //    I will use a flag `forceAuthenticated` set in the `Given` steps.
               //    Default to false (Auth test).
               //    In Timeout/Nav Given steps, I will set `forceAuthenticated = true`.
            }
            
            // IMPLEMENTATION PLAN FOR THIS BLOCK:
            // 1. Reset flags in @Before (not shown, assuming instance scope).
            // 2. `validTellerIdProvided` defaults false.
            // 3. In `Given valid tellerId`, set `validTellerIdProvided = true`.
            // 4. In `Given ... violates timeout`, set `forceAuthenticated = true`.
            // 5. In `Given ... violates navigation`, set `forceAuthenticated = true`.
            // 6. Logic:
            boolean isAuthenticated = validTellerIdProvided || forceAuthenticated;
            
            cmd = new StartSessionCmd(tellerId, terminalId, isAuthenticated);

            resultEvents = aggregate.execute(cmd);
            
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent evt = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", evt.type());
        assertEquals("session-123", evt.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // The implementation throws IllegalStateException or similar runtime exception
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
    
    // --- Flags and Helpers for Scenario detection ---
    private boolean validTellerIdProvided = false;
    private boolean forceAuthenticated = false;

    // Override the Given methods to set flags
    @Override
    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.validTellerIdProvided = true;
    }

    @Override
    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in When
    }

    @Override
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-123");
        aggregate.markStale();
        this.forceAuthenticated = true; // Must pass auth to hit timeout
    }

    @Override
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSession("session-123");
        aggregate.corruptNavigationState();
        this.forceAuthenticated = true; // Must pass auth to hit nav
    }
}