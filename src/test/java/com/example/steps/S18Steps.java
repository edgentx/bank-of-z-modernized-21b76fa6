package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S18Steps {

    private static final String SESSION_ID = UUID.randomUUID().toString();
    private static final String VALID_TELLER_ID = "TELLER_01";
    private static final String VALID_TERMINAL_ID = "TERM_03";
    private static final Instant NOW = Instant.now();

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Ensure default clean state
        aggregate.setLastActivityAt(null);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Set the aggregate's last activity to a future time to simulate 'staleness' or conflict relative to the command
        aggregate.setLastActivityAt(NOW.plusSeconds(3600));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markNavigationContextInvalid();
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in command construction below
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in command construction below
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // For the "violates authentication" scenario, we construct the command with false auth
        boolean isAuthenticated = !aggregate.getClass().getSimpleName().equals("TellerSessionAggregate") || aggregate.getTellerId() == null; 
        
        // Heuristic: if we are in the auth violation scenario (tellerId is null in default state, but specific scenario setup is tricky in this single-step style)
        // Better: rely on a context flag or just constructing a specific command based on the scenario context.
        // However, the simplest way in Cucumber is to construct the command that would succeed, and let the Given step handle the Aggregate state invalidity.
        // BUT: The authentication check is on the COMMAND, not the aggregate state, in the provided code logic.
        // So we need to construct an invalid command for that specific scenario.
        
        // Let's inspect the class name or a flag? No, let's assume the standard command is valid, and we create a specific invalid one for the auth test.
        // Actually, the cleanest way is to parse the scenario title or similar, but we can just check if we are about to trigger an exception.
        
        // Refactoring for clarity based on the specific violation:
        boolean isAuthScenario = System.getProperty("cucumber.filter.name", "").contains("authenticated");
        // Since we can't easily read the scenario name in pure step defs without overhead, we will assume the standard case
        // AND create a specific invalid command setup if the previous Given was the auth one.
        // Since we can't share state easily between methods in this simple pattern, we'll use a default valid command.
        // For the auth violation, we must set authenticated to false.
        
        // Let's deduce intent: The previous @Given set up the aggregate. The Auth violation is likely independent of aggregate state (unlike nav context).
        // So we will default to valid, and if we are in the auth test, we expect the user to have... actually, the step def below for WHEN is shared.
        // We will just execute a VALID command. The Auth test will FAIL if I don't adapt.
        // Correction: The Auth violation implies the command sent should be unauthenticated.
        // Let's assume we execute a valid command for all, except the Auth one where we execute an invalid command.
        // We'll check the aggregate state to decide.
    }
    
    // Overriding the When method with specific logic to handle the Auth scenario
    @When("the StartSessionCmd command is executed with auth context {string}")
    public void the_StartSessionCmd_command_is_executed_with_auth(String authState) {
         boolean authenticated = Boolean.parseBoolean(authState);
         executeCommand(authenticated);
    }

    // We need a generic execute method.
    // Let's use the generic "When the StartSessionCmd command is executed" and handle the variance.
    // The easiest way to distinguish in Cucumber without parameters is hard.
    // I will use a default valid command, and add a small logic check: if the aggregate is marked invalid for Nav, it's a valid command.
    // For the Auth one, the check is on the command.
    // I will inject a specific "Scenario Context" check, or just assume standard.
    // FIX: I will interpret the requirement strictly.
    // 1. Valid command (Auth=True)
    // 2. Auth Violation (Auth=False). The aggregate state doesn't matter for the aggregate logic, but the command does.
    // Since I can't pass params in the generic When, I will check the aggregate state.
    // If the aggregate has no specific state set (default), and I'm testing auth, I need to know.
    // I'll assume the standard flow sends a valid command, and I'll have a separate step for the invalid command? No, Gherkin is fixed.
    
    // Solution: The generic step will try to execute a VALID command.
    // For the Auth test, I will assume the aggregate has been set up in a way that implies this (or I'll just hardcode logic).
    // Better: I will just assume the standard "StartSessionCmd" implies a valid payload, and the Aggregate state handles the rejection.
    // BUT: The Auth logic is `cmd.isAuthenticated()`. This is COMMAND data, not Aggregate state.
    // Therefore, the generic "When" is ambiguous.
    // However, in the absence of specific parameters, I will construct the command dynamically based on a thread-local or just default to valid.
    // To make the test pass, I will default to valid, and for the auth test, I will rely on a hack or a separate specific step.
    // Given the constraint to use the exact Gherkin, I will default to `isAuthenticated = true`.
    // I will ADD an implementation detail to the Aggregate: If the command is valid, but the aggregate state is bad, it fails.
    // For the Auth test, the only way it fails is if I send false.
    // I will assume that for that specific test, I need to detect it.
    // I will use a simple heuristic: if the aggregate is fresh and no other state is set, and it's the "auth" scenario.
    
    // Revised approach: The "Given" for Auth violation does nothing to the aggregate state (it's a fresh aggregate).
    // So I can detect: "Is this a fresh aggregate?". Yes. "Is it the Auth scenario?".
    // I will assume that if the aggregate is in default state and I run the test, it's the Auth test (unlikely, as valid test also starts fresh).
    // Okay, I will assume the standard When sends a VALID command.
    // The Auth test will fail unless I override the command.
    // I'll add a `@Given("the command is unauthenticated")` ? No, Gherkin is fixed.
    
    // I will use reflection/Scenario name detection? No.
    // I will hardcode `isAuthenticated = true` and accept the Auth test might need a different step definition or I have to infer.
    // Wait, if I use `scenarioContext`... not available easily in vanilla Cucumber steps injection without extra setup.
    
    // Let's look at the Gherkin again: "Given a TellerSession aggregate that violates...".
    // Okay, I will construct the command. I will assume that for the purpose of this exercise, I will run the command with `isAuthenticated=true` for ALL steps.
    // The Auth test will fail with the generated code unless I detect it.
    // I will use a workaround: I will check the stack trace or just assume it's the other tests running.
    // Actually, I can just catch the failure and print? No.
    
    // Decision: I will default to `isAuthenticated = true`. The generated code checks `cmd.isAuthenticated()`.
    // To make the "Auth Rejected" test pass, I MUST send false.
    // Since the Gherkin doesn't specify "And the command is unauthenticated", but the scenario title implies it,
    // I will use a flag in the step definition class `isAuthScenario` set by the Given method.
    
    private boolean forceUnauthenticated = false;

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth_impl() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        forceUnauthenticated = true;
    }
    
    // Default Given for valid
    @Given("a valid TellerSession aggregate")
    public void setup_valid_agg() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setLastActivityAt(null);
        forceUnauthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setup_timeout_agg() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setLastActivityAt(NOW.plusSeconds(3600));
        forceUnauthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setup_nav_agg() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markNavigationContextInvalid();
        forceUnauthenticated = false;
    }

    @When("the StartSessionCmd command is executed")
    public void execute_start_session_cmd() {
        boolean authState = !forceUnauthenticated; // True unless forced false
        command = new StartSessionCmd(SESSION_ID, VALID_TELLER_ID, VALID_TERMINAL_ID, authState, NOW);
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(VALID_TELLER_ID, event.tellerId());
        Assertions.assertEquals(VALID_TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception, but command succeeded.");
        // In Java, Domain errors are often modeled as Exceptions (IllegalStateException, IllegalArgumentException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}