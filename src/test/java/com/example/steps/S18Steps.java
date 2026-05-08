package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: StartSessionCmd.
 */
public class S18Steps {

    // Test State
    private TellerSession aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    // Constants for valid state
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_42";
    private static final String VALID_CTX = "MAIN_MENU";
    private static final String VALID_CHANNEL = "TN3270";
    private static final String SESSION_ID = "SESSION_1";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSession(SESSION_ID);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSession(SESSION_ID);
        // The aggregate itself is fine, but the command we will construct will fail the auth check
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesInactivity() {
        // This scenario validates domain rules about inactivity. 
        // In the Start command, this maps to validating the command payload 
        // or the state transition logic.
        // For S-18 StartSessionCmd, we will interpret this as a missing/inactive context
        // or invalid timestamp provided in the command.
        aggregate = new TellerSession(SESSION_ID);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSession(SESSION_ID);
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void aValidTellerIdIsProvided() {
        // Just a setup step context, used in the When block
    }

    @And("a valid terminalId is provided")
    public void aValidTerminalIdIsProvided() {
        // Just a setup step context
    }

    @When("the StartSessionCmd command is executed")
    public void theStartSessionCmdCommandIsExecuted() {
        StartSessionCmd cmd = null;
        
        // Determine context based on previous Givens. 
        // We check the aggregate state or specific flags to simulate different invalid scenarios.
        // Note: In a real Cucumber setup, we'd use Scenario Context or DataTables.
        // Here we use a simple heuristic: 
        // 1. If aggregate exists, assume valid command (Success Scenario).
        // 2. If we are in the "violates auth" scenario (checked via a dummy flag or implied context), send bad command.
        
        // Simulating specific failures based on Scenario titles passed through tags isn't available in pure Java steps
        // without extra setup. We will infer intent based on the session ID or specific state setup in a real project.
        // For this implementation, we will check the state of the aggregate to decide which cmd to send.
        
        // To strictly follow the Gherkin:
        // "Given a valid..." -> Send Valid Cmd
        // "Given... violates Auth" -> Send Unauthenticated Cmd
        
        // We'll assume the success path is default unless we detect the specific violation condition.
        // Since Cucumber scenarios are isolated, we can just check the repository or static state.
        // A cleaner way in this constrained environment is to check the 'currentContext' of the aggregate 
        // (since we didn't set it in constructor, it's null).
        
        // Actually, let's look at the Scenario text matching.
        // Since we can't easily access scenario name in basic Cucumber, we'll rely on the Givens setting the stage.
        
        boolean simulateUnauthenticated = false;
        boolean simulateBadNav = false;
        boolean simulateBadTimeout = false;
        
        // Heuristic to detect violation scenarios based on the Givens called:
        // The violation Givens don't set specific flags in this simple implementation, 
        // so we will default to Valid Cmd for the "Valid" Given.
        // To make the failure tests pass, we need to trigger them.
        
        // We will construct a VALID command. The aggregate will reject it if its internal logic requires it.
        // BUT the StartCmd usually carries the auth status.
        
        // Let's refine: The "Valid" Given was called. We send Valid Cmd.
        // The "Auth" Given was called. We send Invalid Auth Cmd.
        // Since we can't share state between methods easily without a Context class, 
        // we will assume the default for `When` is the SUCCESS case, and for the failures we need to differentiate.
        
        // TRICK: We will look at the Aggregate's state. 
        // If the aggregate was created in the "violates" steps, maybe we can't easily distinguish it from the "valid" step
        // because both just create a new aggregate.
        // 
        // WORKAROUND: Use the aggregate ID to simulate scenario logic, or just check the repository size? No.
        // WORKAROUND: The failure scenarios in the Gherkin explicitly say "aggregate that violates...".
        // Since the aggregate is clean initially, the violation is likely in the *Command*.
        // 
        // We will default to the Success case here.
        // For the Failure scenarios, we must modify this logic or pass a parameter.
        // Given the constraints, I will implement the SUCCESS case logic here.
        // The FAILURE cases need to be triggered. 
        // Since I cannot read the Scenario Title, I will assume the Test Setup for failure scenarios 
        // calls a specific setup method or we just run 4 tests where `this.when` is slightly different.
        // 
        // WAIT! I can use the "Given" methods to set a flag in a shared Context object.
        // Let's create a simple internal state to track the intent.
        
        ScenarioContext context = ScenarioContext.getInstance();
        String intent = context.getIntent();
        
        if ("AUTH_FAIL".equals(intent)) {
            cmd = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, false, VALID_CTX, VALID_CHANNEL);
        } else if ("NAV_FAIL".equals(intent)) {
            // Bad Context
            cmd = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, true, null, VALID_CHANNEL);
        } else if ("TIMEOUT_FAIL".equals(intent)) {
             // Bad Channel/Context (mapped to timeout/source validation in code)
             cmd = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, true, VALID_CTX, null);
        } else {
            // Default / Success
            cmd = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, true, VALID_CTX, VALID_CHANNEL);
        }

        try {
            resultEvents = aggregate.execute(cmd);
            // If successful, save it
            if (!resultEvents.isEmpty()) {
                repository.save(aggregate);
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void aSessionStartedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException, 
            "Expected a domain exception (IllegalState or IllegalArgument), got: " + caughtException.getClass().getSimpleName());
    }
    
    // Helper class to track scenario intent across steps
    private static class ScenarioContext {
        private static final ThreadLocal<ScenarioContext> instance = new ThreadLocal<>();
        private String intent;
        
        public static ScenarioContext getInstance() {
            ScenarioContext ctx = instance.get();
            if (ctx == null) {
                ctx = new ScenarioContext();
                instance.set(ctx);
            }
            return ctx;
        }
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
    }
    
    // Step Definitions hooking into the context
    @Given("a valid TellerSession aggregate1")
    public void setupValid() { ScenarioContext.getInstance().setIntent("SUCCESS"); aValidTellerSessionAggregate(); }
    
    // We need to rename the Given methods slightly or overload to hook the context.
    // Since Cucumber matches regex, we can just use the existing methods but add code.
    // Let's modify the existing violation Givens to set the intent.
    
    // Redefining specific Givens to set Context Intent
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session..")
    public void givenAuthViolation() {
        ScenarioContext.getInstance().setIntent("AUTH_FAIL");
        aTellerSessionAggregateThatViolatesAuthentication();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity..")
    public void givenTimeoutViolation() {
        ScenarioContext.getInstance().setIntent("TIMEOUT_FAIL");
        aTellerSessionAggregateThatViolatesInactivity();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context..")
    public void givenNavViolation() {
        ScenarioContext.getInstance().setIntent("NAV_FAIL");
        aTellerSessionAggregateThatViolatesNavigationState();
    }
}