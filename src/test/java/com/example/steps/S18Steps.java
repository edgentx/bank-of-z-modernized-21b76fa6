package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Handled in context construction
    }

    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Handled in context construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aValidTellerSessionAggregate();
        // We set up the command to be unauthenticated in the 'When' step context, or here via state.
        // The invariant check is on the Command payload in the implementation provided.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesInactivityTimeout() {
        aValidTellerSessionAggregate();
        aggregate.setActive(true); // Simulate session already active/timed out state conflict
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aValidTellerSessionAggregate();
        // Handled in command construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // Default valid context
            String tellerId = "TELLER_123";
            String terminalId = "TERM_01";
            boolean authenticated = true;
            String state = "IDLE";

            // Scenario: Authentication Violation
            if (aggregate.getClass().getSimpleName().equals("TellerSessionAggregate") && !aggregate.isAuthenticated() && aggregate.getVersion() == 0) {
                // This heuristic assumes the "Given" step didn't set authenticated.
                // Since the aggregate starts unauthenticated by default, we flip the command flag to false ONLY if we are in that specific scenario context.
                // To keep step isolation simple, we rely on the specific Given methods modifying the aggregate state which we read here.
                // However, the current implementation puts the auth flag on the Command.
                // Let's check the aggregate state to decide command payload for test clarity.
                if(!aggregate.isAuthenticated()) {
                     authenticated = false;
                }
            }

            // Scenario: Navigation Violation (Context mismatch)
            // We infer this from the "violates navigation" Given step potentially setting a flag, or we just hardcode the payload for the test scenario.
            // Since Given doesn't store state, we construct the specific command based on the description.
            // NOTE: In real Cucumber, we'd use a Scenario Context object. Here we infer.
            if (aggregate.id() != null && aggregate.getVersion() == 0 && !aggregate.isAuthenticated()) {
                 // Reset to default for positive flow if not caught by above logic
                 authenticated = true;
            }
            
            // Refined Logic for Scenarios based on Aggregate State pre-conditioning:
            // 1. Auth Violation: Command must be !authenticated.
            // 2. Timeout: Aggregate is Active.
            // 3. Nav Violation: Command state is INVALID_CONTEXT.

            if (aggregate.isAuthenticated() && !aggregate.isActive() && aggregate.getVersion() == 0) {
                 // Default Positive Flow or Timeout (Handled by aggregate state) 
                 // If we are here, we assume positive flow unless specified.
            }
            
            // Override for Negative Scenarios (Simulated via specific aggregate state setups in Givens)
            // The Givens above call aggregate.setActive(true) or leave default.
            // We need a way to distinguish "Auth" scenario from "Positive" scenario purely programmatically or via helper.
            // For this exercise, we will construct the command based on the specific violation text logic.
            
            // Actually, the cleanest way is to check `aggregate.active` for timeout.
            if(aggregate.isActive()) {
                // This is the Timeout scenario (Active = true, means it's already running)
                authenticated = true; // Valid auth, but rejected by active state
            }
            
            // Differentiating "Auth" scenario from "Positive" is hard without a context object.
            // We will assume Positive by default and override if the exception matches expected text in Then.
            // BUT, to drive the code, we MUST send the invalid payload.
            // Let's look at the Givens again. 
            // "a TellerSession aggregate that violates: A teller must be authenticated..." -> Sets nothing specific yet.
            // We will rely on the fact that `aggregate.isAuthenticated()` defaults to false.
            // So if the Given "aValid..." runs, it defaults to false.
            // If the "Auth" Given runs, it also defaults to false.
            // We need to set it to True for the Positive case.
            
            boolean isPositiveScenario = false;
            // Heuristic: if aggregate has no specific violation markers set (none exist currently), we treat as positive? No.
            // We'll use a thread-local or a simple flag if needed. 
            // For simplicity, we assume the code should handle the Valid Case.
            
            // Let's construct for the VALID case by default and handle the "Auth" failure by NOT setting authenticated = true.
            // Wait, the default for the new aggregate is false. 
            // So: 
            // Positive: authenticated=true.
            // Auth Fail: authenticated=false.
            // Nav Fail: state="INVALID_CONTEXT".
            
            // How to switch? We will look at the current "State" of the test.
            // Hack: We'll use a static string or similar if we were in a real class, but here we just output the code.
            // Let's assume the runner creates a new Steps instance every scenario.
            // We need a way to signal. 
            // The Givens are distinct. We can store a string in the Steps class.
            
            if (this.scenarioType == null) {
                // Default to positive flow setup if not set by Given
                authenticated = true; 
            }

            command = new StartSessionCmd(tellerId, terminalId, authenticated, state);
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }
    
    // Helper to track scenario type from Givens
    private String scenarioType;
    
    @Override
    public void aValidTellerSessionAggregate() {
        super.aValidTellerSessionAggregate();
        this.scenarioType = "VALID";
        // Prepare for positive flow by defaulting auth to true in logic
    }
    
    @Override
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        super.aTellerSessionAggregateThatViolatesAuthentication();
        this.scenarioType = "AUTH_FAIL";
    }
    
    @Override
    public void aTellerSessionAggregateThatViolatesInactivityTimeout() {
        super.aTellerSessionAggregateThatViolatesInactivityTimeout();
        this.scenarioType = "TIMEOUT";
    }
    
    @Override
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        super.aTellerSessionAggregateThatViolatesNavigationState();
        this.scenarioType = "NAV_FAIL";
    }
    
    // Corrected Logic Injection for 'When'
    // Re-implementing the When method logic with the scenarioType flag.
    // (Note: The previous method above is technically valid but incomplete logic. Correcting here via replacement).
    /*
    To ensure the code works, I will rewrite the 'When' method to use the `scenarioType` variable.
    */

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // The error is expected to be a RuntimeException (IllegalStateException)
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }

    // --- Corrected Step Methods with State Tracking ---

    @Override
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate_Final() {
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        // Defaults are valid for starting (auth=false in aggregate, but command will set true)
        this.scenarioType = "POSITIVE";
    }

    @Override
    @Given("a valid tellerId is provided")
    public void aValidTellerIdIsProvided_Final() {
        // No op, handled in when
    }

    @Override
    @Given("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided_Final() {
        // No op, handled in when
    }

    @Override
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth_Final() {
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        this.scenarioType = "AUTH_FAIL";
    }

    @Override
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout_Final() {
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        this.aggregate.setActive(true); // Set state that causes rejection
        this.scenarioType = "TIMEOUT";
    }

    @Override
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNav_Final() {
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        this.scenarioType = "NAV_FAIL";
    }

    @Override
    @When("the StartSessionCmd command is executed")
    public void whenExecuted() {
        try {
            String tId = "T-1";
            String termId = "TERM-1";
            boolean auth = true;
            String state = "IDLE";

            switch (this.scenarioType) {
                case "AUTH_FAIL":
                    auth = false; // Tell aggregate logic to fail
                    break;
                case "NAV_FAIL":
                    state = "INVALID_CONTEXT";
                    break;
                case "TIMEOUT":
                    // Aggregate is already active, command is valid, but aggregate rejects.
                    break;
                case "POSITIVE":
                default:
                    break;
            }
            
            this.command = new StartSessionCmd(tId, termId, auth, state);
            this.resultEvents = this.aggregate.execute(this.command);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
}