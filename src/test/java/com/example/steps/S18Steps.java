package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create valid base command
    private StartSessionCmd createValidCommand() {
        return new StartSessionCmd(
                "session-123",
                tellerId != null ? tellerId : "teller-1",
                terminalId != null ? terminalId : "term-1",
                true, // authenticated
                false, // timedOut
                true // navigationContextValid
        );
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Ensure state is clean/created
        assertEquals(TellerSessionAggregate.SessionState.CREATED, aggregate.getState());
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-42";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "terminal-101";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-123");
        this.tellerId = "teller-42";
        this.terminalId = "terminal-101";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-123");
        this.tellerId = "teller-42";
        this.terminalId = "terminal-101";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_context() {
        aggregate = new TellerSessionAggregate("session-123");
        this.tellerId = "teller-42";
        this.terminalId = "terminal-101";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd;
        
        // Determine violation type based on context (simulated via state or checks)
        // Here we construct the specific command that triggers the failure based on the scenario context
        if (aggregate.getState() == TellerSessionAggregate.SessionState.CREATED) {
            // Default valid context, check if we need to force a violation
            // This is a simplification for the Cucumber steps; in a real app we might use tags
            // We rely on the specific Given steps to set up the 'vibe' or we infer it
            // For simplicity in this stateless step def, we create a command that might fail
            // However, to match specific scenarios, let's assume we create a command that 
            // would fail the specific invariant check.
            
            // We will use a trick: if tellerId is "VIOLATE_AUTH", we set auth false.
            // For the purpose of this exercise, we'll construct the command based on the scenario title
            // or helper flags. But to be precise to the prompt's Gherkin:
        }

        // We need to map the "Given... violates" steps to specific command properties.
        // Since Cucumber steps are isolated, we can't pass flags easily without instance variables.
        // We will assume standard valid command unless overridden.
        cmd = createValidCommand();
        
        // Apply violations based on logic derived from scenario context (simulated)
        if (tellerId == null) tellerId = "teller-1";
        if (terminalId == null) terminalId = "term-1";

        // Heuristic to determine which scenario we are in (simple hack for the sake of the code block)
        // In a robust setup, we'd have a context object.
        // Here, let's check the stack trace or just try-catch?
        // Better: Let's look at the aggregate state or specific flags.
        // Let's assume the "Given" methods set specific flags.
        
        boolean forceAuthViolation = false;
        boolean forceTimeoutViolation = false;
        boolean forceNavViolation = false;

        // Check if we are in the specific violation scenarios
        // Since we can't share state easily between Given methods without fields, we rely on fields.
        // Let's assume the default command is valid. We need to override it if the scenario expects a failure.
        // We can infer this from the `aggregate` state or specific flags.
        // Since the `aggregate` is just created in the Given methods, it doesn't hold the violation type.
        // We will use a special string in tellerId to signal violations for this demo, 
        // or better, just construct the command to be valid and see if it fails.
        // Wait, the "Given" steps for violations say "Given a TellerSession aggregate that violates...".
        // It implies the COMMAND will violate it, or the AGGREGATE state violates it.
        // The prompt says: "A teller must be authenticated". This is usually a Command check or external Auth check.
        // The prompt says: "Sessions must timeout". This is a state check.
        // The prompt says: "Navigation state...". This is a Command check.
        
        // Let's assume the Step Definitions set up the command to cause the failure.
        // I will modify the Command creation logic slightly to inspect the scenario context if possible,
        // or just default to Valid. 
        // *However*, the scenarios require REJECTIONS.
        // Let's assume the `tellerId` field is used to trigger the violation for the sake of the test mapping.
        // e.g. if tellerId is "teller-42" (from the given steps), it's valid.
        // The prompt says: "Given a TellerSession aggregate that violates...".
        // This suggests the AGGREGATE is in a bad state?
        // "A teller must be authenticated" -> The command usually carries the auth token.
        // "Sessions must timeout" -> The aggregate might be timed out?
        // "Navigation state..." -> The command carries bad navigation state.

        // Let's refine the Command creation:
        // 1. Default to Valid.
        // 2. If the scenario implies rejection, we modify the command.
        // Since we can't detect the scenario name easily, we rely on the fact that 
        // the violation Givens don't set a specific 'invalid' flag in this class structure.
        // We will add flags to the class for this.
    }

    // We need to distinguish the scenarios. I'll add simple boolean flags to the class to track the 'Given' context.
    private boolean scenarioIsAuthViolation = false;
    private boolean scenarioIsTimeoutViolation = false;
    private boolean scenarioIsNavViolation = false;

    // Overriding the Given methods to set flags
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupAuthViolation() {
        a_valid_teller_session_aggregate(); // reset
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        this.scenarioIsAuthViolation = true;
        this.scenarioIsTimeoutViolation = false;
        this.scenarioIsNavViolation = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setupTimeoutViolation() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        this.scenarioIsAuthViolation = false;
        this.scenarioIsTimeoutViolation = true;
        this.scenarioIsNavViolation = false;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setupNavViolation() {
        a_valid_teller_session_aggregate();
        a_valid_teller_id_is_provided();
        a_valid_terminal_id_is_provided();
        this.scenarioIsAuthViolation = false;
        this.scenarioIsTimeoutViolation = false;
        this.scenarioIsNavViolation = true;
    }

    @When("the StartSessionCmd command is executed")
    public void executeStartSessionCmd() {
        try {
            boolean auth = !scenarioIsAuthViolation;
            boolean timeout = scenarioIsTimeoutViolation;
            boolean nav = !scenarioIsNavViolation;

            StartSessionCmd cmd = new StartSessionCmd(
                "session-123",
                tellerId,
                terminalId,
                auth,
                timeout,
                nav
            );
            resultEvents = aggregate.execute(cmd);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultEvents = null;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
