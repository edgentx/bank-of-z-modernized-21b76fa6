package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private String sessionId = "sess-123";
    private String validTellerId = "teller-001";
    private String validTerminalId = "term-A";
    private String validScreen = "MAIN_MENU";
    private long validTimeout = 30; // minutes

    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Data setup for scenario context
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Data setup for scenario context
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, validTellerId, validTerminalId, true, validScreen, validTimeout);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertTrue(event instanceof SessionStartedEvent);
        
        SessionStartedEvent started = (SessionStartedEvent) event;
        assertEquals(sessionId, started.aggregateId());
        assertEquals(validTellerId, started.tellerId());
        assertEquals(validTerminalId, started.terminalId());
        assertEquals(validScreen, started.initialScreen());
        assertTrue(started.sessionTimeoutAt().isAfter(Instant.now()));
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // We simulate the violation by providing an invalid timeout duration in the command (via context switch)
        // This step definition sets up the aggregate, the modification happens in the context of the scenario execution logic usually,
        // but here we will modify the 'validTimeout' context variable to 0 or negative.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = new TellerSessionAggregate(sessionId);
        // We simulate the violation by providing a null/blank screen in the command.
    }

    // Specific When/Then for rejection scenarios might require different parameter injection.
    // However, Cucumber matches the text. We can overload or check conditions inside a generic When, or use specific Whens.
    // Given the prompt asks for specific scenarios with specific violations, we will adjust the command parameters based on the scenario state.
    // To keep it simple and robust for Cucumber, we'll use a dedicated When for the rejection scenarios or check the violation type.
    
    // Simpler approach: The prompt implies the state of the aggregate or the command context causes the violation.
    // The "Given ... violates" step sets up the context.
    
    // Overriding the When for the negative cases specifically for clarity.
    @When("the StartSessionCmd command is executed with the violating context")
    public void the_start_session_cmd_command_is_executed_with_violation() {
        try {
            // We inspect the scenario context implicitly. Since we can't pass state easily between methods in simple Cucumber classes without instance variables, 
            // we will assume the violation is triggered by modifying the command payload based on what the Given set up.
            // However, the Gherkin text is identical to the positive flow's When line.
            // Let's make the primary When method generic enough to handle failures, or just use the same one.
            // Actually, the Gherkin text is IDENTICAL for all scenarios for the When step.
            // So `the_start_session_cmd_command_is_executed()` will be called for all.
            // We need a way to differentiate.
            
            // Strategy: The "Given" steps will set flags or modify the `valid...` fields to be invalid.
            // But the Givens provided in the prompt text are: "Given a TellerSession aggregate that violates: ..."
            // Let's implement the violation logic IN the Given step by modifying the instance variables used by the When step.
            
            // We need to distinguish which violation we are testing. 
            // The generic `the_start_session_cmd_command_is_executed()` above uses `valid...` fields.
            // We need to trigger the specific violation.
            // Since I cannot change the Gherkin, I must infer the violation from the state.
            // I will rely on the fact that the previous step (Given) set the aggregate into a specific 'mode' or modified the data.
            
            // To make this work reliably without a complex state machine, I will simply check the specific violation string in the Given method
            // and set a variable `violationType`. The When method will check this variable.
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Actually, looking at the Gherkin again, the "When" lines are identical. 
    // Cucumber will match `the_start_session_cmd_command_is_executed` for all scenarios.
    // But we need to send different commands to trigger the different errors.
    // The previous code for `the_start_session_cmd_command_is_executed` always sends a VALID command.
    // We need to fix that. We need to know *which* scenario is running.
    
    // Let's refine the implementation below to be stateful based on the "Given" calls.
    
    private String violationType = "NONE";

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void auth_violation() {
        a_valid_teller_session_aggregate();
        this.violationType = "AUTH";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void timeout_violation() {
        a_valid_teller_session_aggregate();
        this.violationType = "TIMEOUT";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void nav_violation() {
        a_valid_teller_session_aggregate();
        this.violationType = "NAV";
    }

    // Re-implementing the WHEN to handle the state
    @When("the StartSessionCmd command is executed")
    public void execute_start_session() {
        try {
            boolean isAuth = true;
            String screen = validScreen;
            long timeout = validTimeout;

            if ("AUTH".equals(violationType)) {
                isAuth = false;
            } else if ("TIMEOUT".equals(violationType)) {
                timeout = 0; // Invalid
            } else if ("NAV".equals(violationType)) {
                screen = null; // Invalid
            }

            StartSessionCmd cmd = new StartSessionCmd(sessionId, validTellerId, validTerminalId, isAuth, screen, timeout);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Check for expected exception types (IllegalStateException for invariants)
        assertTrue(thrownException instanceof IllegalStateException);
    }

}
