package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Standard valid data
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_42";
    private static final String VALID_SESSION_ID = "SESSION_123";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup - values used in the When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup - values used in the When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // No specific state needed on aggregate for this specific command logic,
        // the violation is triggered by the Command payload in the 'When' step.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // The violation is triggered by the Command payload simulating staleness.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // The violation is triggered by the Command payload.
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Determine context from previous Given steps to decide which violation to inject.
        // We inspect the exception or a flag if we were storing state, but here we infer
        // based on the test execution flow. A cleaner way in Cucumber is Scenario Outline,
        // but we will use the exception type to differentiate.
        
        // We'll default to a valid command, and override in catch blocks if needed? 
        // Better: We assume valid unless a specific state was set.
        // Since Cucumber steps don't pass state easily, we rely on the "violates" Given
        // to perhaps set a flag, or we just try to execute and see what happens.
        // However, the Command itself carries the flags.

        // To distinguish, we need to know which scenario we are in.
        // We will use a heuristic or create a default command.
        // For strict BDD, we should use Scenario Context. Let's simulate the valid one by default.
        
        executeCommand(true, true, false);
    }

    // Helper to allow overriding flags for specific scenarios logic mapping
    private void executeCommand(boolean isAuthenticated, boolean isContextValid, boolean isStale) {
        StartSessionCmd cmd = new StartSessionCmd(
                VALID_SESSION_ID,
                VALID_TELLER_ID,
                VALID_TERMINAL_ID,
                isAuthenticated,
                isContextValid,
                isStale
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // We need specific @When methods or intercept the logic to set the violation flags.
    // Since the Gherkin is generic, we will map the "violates" scenarios by checking
    // if the aggregate is in a specific mode, or we can just assume the test setup
    // implies the violation. 
    // *Workaround*: I will modify the 'When' logic to check for a specific marker or just 
    // let the generic 'When' run a valid command, and manually wire the violations via 
    // detecting the specific step combination? No, that's brittle.
    // I will add specific step definitions that technically match the Gherkin but allow parameter passing? 
    // No, the Gherkin is fixed. 
    // I will use a ThreadLocal or field to signal the violation type.

    private String violationType;

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setViolationAuth() { violationType = "AUTH"; aggregate = new TellerSessionAggregate(VALID_SESSION_ID); }
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setViolationTimeout() { violationType = "TIMEOUT"; aggregate = new TellerSessionAggregate(VALID_SESSION_ID); }
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setViolationNav() { violationType = "NAV"; aggregate = new TellerSessionAggregate(VALID_SESSION_ID); }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed_with_context() {
        if ("AUTH".equals(violationType)) {
            executeCommand(false, true, false);
        } else if ("TIMEOUT".equals(violationType)) {
            executeCommand(true, true, true);
        } else if ("NAV".equals(violationType)) {
            executeCommand(true, false, false);
        } else {
            // Valid case
            executeCommand(true, true, false);
        }
        // Reset for next scenario
        violationType = null;
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event type mismatch");
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(VALID_SESSION_ID, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Exception should be thrown");
        // We expect IllegalStateException for domain invariant violations
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException, got " + capturedException.getClass().getSimpleName());
        
        // Verify specific messages based on the violation type context if desired
        // (Optional strictly, but good for completeness)
    }
}
