package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Standard test data
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_3270_01";
    private static final String VALID_SESSION_ID = "SESSION_01";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // We will pass isAuthenticated=false in the When step
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // We will pass isTimedOut=true in the When step
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // We will pass invalid nav state in the When step
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Context usually handled in the When step construction
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Context usually handled in the When step construction
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        // Default valid command attributes
        boolean isAuthenticated = true;
        boolean isTimedOut = false;
        String navState = "HOME_SCREEN";

        // If we are in a violation scenario, the aggregate state isn't the only factor,
        // the test context usually implies we are calling it with specific parameters or in a specific state.
        // Based on the structure, we'll check if the aggregate setup implies a specific command failure.
        // However, the Gherkin "Given aggregate that violates" implies the AGGREGATE is in a bad state,
        // OR the command being sent creates a violation.
        // Since StartSessionCmd creates the session, the violations are parameter-dependent.
        // We will infer the intent based on the setup method names.
        
        // Heuristic to match the specific scenario Givens to Command parameters:
        if (aggregate.getClass().getSimpleName().equals("TellerSessionAggregate")) {
             // We can't inspect the violation intent from the aggregate instance easily without state.
             // We will rely on the fact that the previous setup methods don't set specific flags on the aggregate itself.
             // Instead, we will use the specific violation Givens to modify how we construct the command.
             
             // Note: In a real framework, we might store 'intendedViolation' in the context.
             // Here we use a simplified approach: The test setup usually runs strictly.
             // We will default to valid, but if the stack trace or test name hinted otherwise...
             // For this generated code, we must assume the "Given" implies the command data.
        }
        
        // Refining logic to support specific scenarios:
        // 1. "Given ... violates: Auth" -> Command with Auth=false
        // 2. "Given ... violates: Timeout" -> Command with Timeout=true
        // 3. "Given ... violates: Nav" -> Command with Nav="INVALID_CONTEXT"
        // We detect these by creating specific setup state or just hardcoding the scenarios in the When.
        // Given the constraints, we will interpret the scenarios based on the step text context.
        
        // Attempting to detect scenario via a thread-local or static flag is messy.
        // Instead, we will construct a VALID command. If the test requires a rejection, it implies
        // we should be passing invalid data. Since Cucumber doesn't pass arguments to "When", 
        // we have to rely on context set in "Given".
        
        // Let's use a simplified context switch:
        String scenarioContext = getViolationContext();

        if (scenarioContext != null) {
            switch (scenarioContext) {
                case "AUTH": isAuthenticated = false; break;
                case "TIMEOUT": isTimedOut = true; break;
                case "NAV": navState = "INVALID_CONTEXT"; break;
            }
        }

        StartSessionCmd cmd = new StartSessionCmd(
            VALID_SESSION_ID,
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            isAuthenticated,
            isTimedOut,
            navState
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Helper to map violation Givens to Command parameters
    private String getViolationContext() {
        // Since we can't easily share state between Givens and When without a field, 
        // and we have multiple Givens, we check if the aggregate is in a specific state or 
        // we set a flag in the Given methods.
        // However, looking at the Provided Givens, they don't set a flag.
        // We will modify the Given methods to set a private field.
        return violationContext;
    }

    private String violationContext;

    // Override Givens to set context flags
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setAuthViolation() {
        aTellerSessionAggregateThatViolatesAuthentication();
        this.violationContext = "AUTH";
    }
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setTimeoutViolation() {
        aTellerSessionAggregateThatViolatesTimeout();
        this.violationContext = "TIMEOUT";
    }
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setNavViolation() {
        aTellerSessionAggregateThatViolatesNavigationState();
        this.violationContext = "NAV";
    }
    // Clear context for the happy path
    @Given("a valid TellerSession aggregate")
    public void resetContext() {
        aValidTellerSessionAggregate();
        this.violationContext = null;
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(VALID_SESSION_ID, event.aggregateId());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException);
        // Verify the error messages match the invariants
        assertTrue(caughtException.getMessage().contains("must be authenticated") ||
                   caughtException.getMessage().contains("timeout") ||
                   caughtException.getMessage().contains("Navigation state"));
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvidedForSetup() {
        // Placeholders to satisfy Gherkin mapping if called explicitly
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvidedForSetup() {
        // Placeholders
    }
}
