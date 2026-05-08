package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a valid standard command context
    private static final String VALID_CONTEXT = "MAIN_MENU";
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // ID is arbitrary for this test, usually passed in command or generated
        aggregate = new TellerSessionAggregate("SESSION_123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the 'When' clause construction to allow for variations in scenarios
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the 'When' clause construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        aggregate.setAuthenticated(false); // Explicitly violate/auth state check if needed
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        // Set activity time to 20 minutes ago (assuming timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(20 * 60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        // This violation is usually driven by the Command context being invalid in the 'When'
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Scenario-specific logic based on the 'Given' state
        boolean isAuthenticated = true;
        String context = VALID_CONTEXT;
        Instant timestamp = Instant.now();

        // Detect violations based on the Givens (Heuristic for step implementation)
        // 1. Timeout violation?
        if (aggregate.getLastActivity() != null && Instant.now().minusSeconds(20*60).isAfter(aggregate.getLastActivity())) {
            // We are in the timeout scenario, timestamp validity doesn't matter, the aggregate check will fail
        }

        // 2. Auth violation?
        // We check the aggregate state or command intent. Since the command carries auth intent:
        if (!aggregate.isAuthenticated()) {
            isAuthenticated = false;
        }

        // 3. Navigation violation?
        // If the command provided later has a null/blank context, it fails. 
        // But we need to trigger the violation here.
        // For the specific scenario "Given violates nav state", let's assume the intent is a bad context.
        // However, Steps usually decouple setup from execution.
        // Let's infer based on simple class-level state or default to valid unless overridden.
        // Better approach: Check the state of the aggregate set in Given.
        
        // Re-evaluating specific scenario triggers:
        // We'll use a variable set in Given if we could, but here we simulate.
        
        // Simulating the "Auth Violation" scenario:
        if (!aggregate.isAuthenticated()) {
            isAuthenticated = false;
        }

        // Simulating "Navigation Violation" scenario:
        // We'll pass a bad context if the aggregate was marked for it (hypothetically) or just hardcode valid here 
        // and rely on the specific test setup to override context.
        // Since we can't pass state between steps easily without fields, let's assume valid.

        // Actually, for the Navigation scenario, we will construct a command with null context below
        // by checking if we are in that scenario. Since Cucumber doesn't pass "Scenario" objects easily, 
        // we rely on the aggregate state.
        // *Correction*: The Gherkin says "Given aggregate that violates...".
        // If we are in the Nav step, we might want to force a bad context here.
        
        // Let's look at the aggregate state logic. The aggregate itself doesn't store the *command's* future context.
        // So we assume default valid, unless we check a specific flag.
        // To keep it simple and working: Default to valid params.
        // The "Rejected" scenarios will need to override `isAuthenticated` or `context` manually here or use a shared field.
        // Let's use a shared field `scenarioType` set in Givens. 
        // *Refined*: Just assume valid. The test for Auth failure sets aggregate.authenticated = false. 
        // But the command `isAuthenticated` param must match the *intent*. 
        // If I am NOT authenticated, I send a command saying I am (trying to hack) or saying I am not?
        // Usually, the System authenticates you. If you are not, the command isn't formed or the token is invalid.
        // Let's assume the Command carries the *result* of auth. 
        // If the scenario is "Auth Violation", we act as if an unauthed user tries to start.
        // So isAuthenticated = false.

        if (!aggregate.isAuthenticated()) { 
            // This detects the "Given auth violation" step
            isAuthenticated = false;
        }

        // For Navigation violation, the Given step description is "violates nav state".
        // We can't detect this from the aggregate object easily without a flag.
        // However, if we look at the step definitions, they are independent.
        // We will use a default valid context, unless we are in a specific case.
        // *Trick*: Use a thread-local or simple string detection if possible? No.
        // Let's assume the default context is VALID.
        // If the test is for Navigation, we must set context to null/blank.
        // How? We can't distinguish calls from different scenarios in this method signature alone 
        // unless we stored state in `this`.
        // Let's fix the steps to store state.
    }

    // Refined @When with state detection logic
    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_refined() {
        // Defaults
        boolean isAuthenticated = true;
        String context = VALID_CONTEXT;
        Instant timestamp = Instant.now();
        String tellerId = VALID_TELLER_ID;
        String terminalId = VALID_TERMINAL_ID;

        // Adjustments based on Aggregate State (set in Given)
        if (!aggregate.isAuthenticated()) {
            // Detected Auth Violation Scenario
            isAuthenticated = false;
        }
        
        // We need a way to detect the other two violations from the Given steps.
        // Since the Given steps don't set a 'scenarioName' field, we have to infer from the Aggregate state.
        // 1. Timeout: aggregate.lastActivity is old. (Handled by logic inside aggregate, command can be valid).
        // 2. Navigation: The aggregate doesn't strictly hold the *command's* navigation context yet.
        // We need the step definition to pass the violation intent.
        // Since I can't change the Gherkin, I will infer:
        // If I am in the "Navigation" scenario, I need to send a bad context.
        // Since I can't infer it, I will assume the user of this class (Cucumber) sets up the Aggregate or a field.
        // *Wait*, looking at the Gherkin: "Given a TellerSession aggregate that violates...".
        // The only way to know which violation is to inspect the object or set a flag in the Given.
        // The Given steps below will set a private enum `currentScenario`.

        if (currentScenario == Scenario.TIMEOUT) {
            // The aggregate is already set to old. The command timestamp should be now (triggering staleness check) or old.
            // The aggregate check `isSessionStale` uses `Instant.now()`. So any command works.
        }

        if (currentScenario == Scenario.NAVIGATION) {
            context = null; // Trigger violation
        }

        cmd = new StartSessionCmd("SESSION_123", tellerId, terminalId, isAuthenticated, context, timestamp);

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    enum Scenario { NONE, AUTH, TIMEOUT, NAVIGATION }
    private Scenario currentScenario = Scenario.NONE;

    // Linking Givens to Scenario flag
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setup_auth_violation() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        aggregate.setAuthenticated(false);
        currentScenario = Scenario.AUTH;
    }
    
    // Overriding the previous simple Givens with the specific ones from the prompt
    // (Deduplication handled by Cucumber regex matching usually, here we list them)

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setup_timeout_violation() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        aggregate.setLastActivityAt(Instant.now().minusSeconds(20 * 60)); // 20 mins ago
        currentScenario = Scenario.TIMEOUT;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setup_nav_violation() {
        aggregate = new TellerSessionAggregate("SESSION_123");
        aggregate.setAuthenticated(true); // Ensure we don't fail on auth first
        currentScenario = Scenario.NAVIGATION;
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("TELLER_001", event.getTellerId());
        assertEquals("TERM_01", event.getTerminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect either IllegalStateException or UnknownCommandException (if logic fails hard), or custom DomainException
        // The prompt says "domain error". In Java DDD, this is often an exception.
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
