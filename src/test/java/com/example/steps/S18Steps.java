package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    private String validTellerId = "TELLER_001";
    private String validTerminalId = "TERM_A01";
    private String validSessionId = "SESSION_001";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(validSessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(validSessionId);
        // Simulate unauthenticated state by passing invalid data in command later
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(validSessionId);
        aggregate.markSessionTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(validSessionId);
        aggregate.markNavigationStateInvalid();
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Setup implied by validTellerId default
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Setup implied by validTerminalId default
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        try {
            // We check for 'violations' by modifying the command inputs or aggregate state in previous steps
            // For the auth violation, we pass null/blank tellerId
            String tellerToUse = (aggregate != null && aggregate.getClass().getSimpleName().contains("ViolatesAuthentication")) 
                                 ? "" : validTellerId;
                                 // Logic adjusted: In step definitions above we just set state. 
                                 // Let's rely on specific state setup in the aggregate for the negative tests.
                                 
            // The "Authentication" violation is handled by passing invalid data in the Command.
            // The "Timeout" and "Navigation" violations are handled by Aggregate state.
            
            // Determine context based on aggregate state if possible, or we assume the generic path
            String tId = validTellerId;
            String termId = validTerminalId;
            
            // Simple heuristic to detect which scenario we are in to provide invalid input for auth
            // (In a real world we might use scenario outline or context injection, but this works for unit steps)
            if (aggregate != null && !aggregate.markSessionTimedOut() /*dummy check*/ && aggregate.getStatus() == TellerSessionAggregate.Status.IDLE) {
                 // Check if we are in the auth violation scenario? 
                 // Since the aggregate doesn't have an 'isAuthViolation' flag, we rely on the specific Given steps.
                 // The 'Authentication' scenario doesn't set aggregate state, it requires bad Command input.
                 // However, Cucumber scenarios are isolated. We will handle the specific trigger inside the try/catch.
            }

            // Strategy: We execute the command. If the aggregate is in a 'Violated' state (Timeout/Nav), it throws.
            // If the scenario is Auth violation, we must send bad command data.
            // We can detect the Auth scenario if the aggregate is NOT marked timed out or invalid nav.
            
            boolean isAuthViolationScenario = false;
            // A bit of a hack to detect the specific scenario for the command payload
            // Since we can't pass context between steps easily without a shared context object, 
            // we infer from the current aggregate setup.
            // If the aggregate is NOT marked with timeout or nav error, and we want to test auth error, we pass bad input.
            // But we need to know WHICH scenario. Let's look at the 'stacktrace' of sorts or just explicit logic.
            
            // Refinement: The Given steps set specific flags. Let's add a flag to the aggregate purely for testing? No.
            // Let's use the repo or a shared variable.
            // For simplicity in this generated code: 
            // If the 'violated' method was called, the exception will come from the aggregate logic.
            // The Auth violation MUST come from the command.
            // So we need a way to know. 
            // Let's assume the standard valid input, unless we are in a specific state.
            // But we don't have 'state' for Auth.
            // Solution: The 'Given' for Auth violation will set a static flag or we use a specific ID.
            // Let's make 'validTellerId' null or empty ONLY for that scenario.
            
            // Re-reading Gherkin: "Given a TellerSession aggregate that violates: A teller must be authenticated..."
            // This implies the aggregate is in a state where auth is failed?
            // But the command carries the auth. The aggregate just checks it.
            // Let's assume the command carries the auth token.
            // I will default to Valid Inputs. The test for Auth Violation will need to modify the input.
            
            StartSessionCmd cmd = new StartSessionCmd(validSessionId, tId, termId);
            resultEvents = aggregate.execute(cmd);
            
        } catch (Exception e) {
            capturedException = e;
        }
    }
    
    // Overriding When for Auth violation specifically to pass bad data
    @When("the StartSessionCmd command is executed with invalid auth")
    public void theStartSessionCmdCommandIsExecutedWithInvalidAuth() {
        // This is strictly for the Auth scenario if we map it explicitly.
        // However, to adhere strictly to the single 'When' clause in the feature file provided:
        // "When the StartSessionCmd command is executed"
        // We must handle the branch inside the step or via a context.
        // Since the feature file is identical, I will modify the main When method to detect context.
    }
    
    // Revised approach for S18Steps to handle the single 'When' clause mapping to multiple scenarios:
    // We will inspect the aggregate state. 

    @Given("setup Auth Violation Scenario")
    public void setupAuthViolation() {
        this.validTellerId = ""; // Blank ID to violate auth
    }

    // NOTE: The Gherkin provided in the prompt has:
    // Given a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.
    // This maps to S18Steps.aTellerSessionAggregateThatViolatesAuthentication()
    // I will update that method to set the validTellerId to empty.

    @Override
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(validSessionId);
        this.validTellerId = ""; // Invalid
        this.validTerminalId = "TERM_A01";
    }

    // Positive scenario reset
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregateReset() {
        aggregate = new TellerSessionAggregate(validSessionId);
        this.validTellerId = "TELLER_001";
        this.validTerminalId = "TERM_A01";
    }


    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "Events should not be empty");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
