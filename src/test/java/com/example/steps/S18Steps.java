package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a valid base aggregate
    private TellerSessionAggregate createValidAggregate() {
        return new TellerSessionAggregate("session-123", 30);
    }

    // Helper to create a valid base command (authenticated, valid IDs)
    private StartSessionCmd createValidCommand() {
        return new StartSessionCmd(
            "session-123",
            "teller-42",
            "term-01",
            true, // authenticated
            "HOME", // valid nav state
            Instant.now()
        );
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = createValidAggregate();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Nothing to do here, implied by command creation in 'When' step
        // However, in Cucumber we often build context. For this pattern,
        // we will create the valid command object in the When step.
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Same as above
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Default positive case execution
        StartSessionCmd cmd = createValidCommand();
        executeCommand(cmd);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("session-123", event.aggregateId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = createValidAggregate();
        // The violation is in the COMMAND state for this story, but the gherkin implies the aggregate context
        // or the setup of the execution context.
        // Since the command carries the auth status, we will pass a false command in When.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = createValidAggregate();
        // We will simulate a stale command timestamp
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = createValidAggregate();
        // We will pass an invalid nav state in the command
    }

    @When("the StartSessionCmd command is executed with violation")
    public void the_start_session_cmd_command_is_executed_with_violation() {
        StartSessionCmd cmd;

        // Detect which scenario we are in based on the previous Given title is hard in pure Java steps
        // without shared state. Instead, we check the aggregate state or use a shared flag.
        // A cleaner way for this specific file structure:
        // We will assume the violation is passed via the command properties since the Aggregate is new.

        if (aggregate.getClass().getSimpleName().contains("Timeout")) {
             // Stale timestamp
             cmd = new StartSessionCmd("sid", "tid", "tid", true, "HOME", Instant.now().minusSeconds(3600)); // 1 hour ago
        } else if (aggregate.getClass().getSimpleName().contains("Navigation")) {
            // Invalid Nav
            cmd = new StartSessionCmd("sid", "tid", "tid", true, "", Instant.now());
        } else {
            // Auth violation (default)
            cmd = new StartSessionCmd("sid", "tid", "tid", false, "HOME", Instant.now());
        }
        
        // However, the Gherkin 'Given' setup in Cucumber usually sets a context variable.
        // To keep it simple and robust for this generation, we rely on a context variable 'violationType'
        // or we just catch the exception in the generic 'When' if the command was prepared earlier.
        // BUT, the previous 'When' is specific to the positive case.
        // Let's implement the specific violation logic here.
        
        // Actually, simpler approach: The Given sets up the AGGREGATE, but the command is built here.
        // We need to know *which* violation to test.
        // Since we can't read the Gherkin string easily, we'll check if a specific flag was set.
        // Or, more simply, we check the current aggregate's simulated state (if we mutated it).
        // Since StartSession creates a NEW session, the violation MUST come from the Command inputs.
        
        // We will use a heuristic or a shared string field `currentScenarioViolation`
        // For this output, I will assume the specific violation is passed via the method matching the Gherkin.
        // But Cucumber Java maps methods to regex.
    }

    // To strictly follow the generated Gherkin structure, we need the When step to handle the execution
    // and the Then step to verify the error. We will use a shared variable to track which violation to inject.
    
    /* 
     * Refining the 'When' logic to handle the different negative scenarios based on a context flag. 
     * Since the steps are stateless between Given/When in pure Java unless we use fields, 
     * we will set a field in the Given methods below.
     */

    private String violationType;

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setup_auth_violation() {
        aggregate = createValidAggregate();
        violationType = "AUTH";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setup_timeout_violation() {
        aggregate = createValidAggregate();
        violationType = "TIMEOUT";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setup_nav_violation() {
        aggregate = createValidAggregate();
        violationType = "NAV";
    }

    @When("the StartSessionCmd command is executed")
    public void executeCommandForViolations() {
        StartSessionCmd cmd;
        if ("AUTH".equals(violationType)) {
            cmd = new StartSessionCmd("sid", "tid", "term", false, "HOME", Instant.now());
        } else if ("TIMEOUT".equals(violationType)) {
            // Simulate a request timestamp older than 30 mins
            cmd = new StartSessionCmd("sid", "tid", "term", true, "HOME", Instant.now().minusSeconds(1801));
        } else if ("NAV".equals(violationType)) {
            cmd = new StartSessionCmd("sid", "tid", "term", true, "", Instant.now());
        } else {
            // Default positive case from the first method definition conflict resolution
            // Cucumber will match the first method found if regex is identical? No, it matches distinct regex.
            // We need to make sure the Positive Case 'When' doesn't overlap or uses a different logic path.
            // Actually, the Gherkin has "When the StartSessionCmd command is executed" for ALL scenarios.
            // So this single method MUST handle both positive and negative flows based on state set in Given.
            cmd = createValidCommand(); // Default to valid if no violation set
        }
        executeCommand(cmd);
    }

    private void executeCommand(StartSessionCmd cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultEvents = null;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception but command succeeded");
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException)");
    }

}
