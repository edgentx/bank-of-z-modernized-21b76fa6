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

import java.time.Duration;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Value provided in the 'When' step via Command object
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Value provided in the 'When' step via Command object
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        // Forcefully set state to invalid context if we had a setter, 
        // but per invariants, we simulate the violation by testing against the command context 
        // or assuming the aggregate was constructed in a bad state.
        // Since we start fresh, the command execution will handle the state check.
        // To force the specific error "Navigation state must accurately reflect...", 
        // we rely on the implementation checking the initial state or the command payload.
        // Assuming the 'StartSessionCmd' logic checks the aggregate's current nav state.
        // The aggregate default is IDLE. To violate, we would need to be in a state other than IDLE.
        // As we cannot manually set state easily without a setter (which is good for encapsulation),
        // we will assume the aggregate handles transitions internally.
        // However, to fulfill the Given, we might mock a scenario where the system thinks it's in a different state.
        // For this exercise, we assume the aggregate starts valid, but we might need to adjust the command.
        // Actually, the standard aggregate starts IDLE, which IS valid for starting a session.
        // To violate "Navigation state must accurately reflect current operational context",
        // it usually implies the command context (e.g. requesting START from a state that isn't IDLE)
        // or the UI state is out of sync.
        // Given the constraints, we will rely on the logic in the aggregate.
        // *Modification*: To properly test this failure case without modifying the Aggregate to expose setters,
        // we check if the `StartSessionCmd` logic allows starting from an active state.
        // Let's assume the violation is that the Terminal is already in use or state is invalid.
        // We will interpret the violation as: The aggregate is ALREADY active/started.
        
        // Hack for testing: Since we cannot change state without commands, we'll execute a dummy command if possible
        // or just accept that the initial IDLE state is valid and this test might pass unless we add more logic.
        // *Refinement*: The prompt asks to test the invariant. If the invariant is "Can only start if IDLE",
        // and we are IDLE, it passes. To make it fail, we need to be NOT IDLE.
        // Since we can't set state, we will simply assume the aggregate validates something else or 
        // we accept that this specific scenario might be hard to trigger without history.
        // However, looking at the aggregate code: `if (!"IDLE".equals(this.navigationState))`.
        // The default IS IDLE. So this scenario as written would pass successfully.
        // To make it fail (violate the invariant), we would need to start the session twice. 
        // We will handle the "twice" logic in the When step if needed, but here we just setup the object.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd("teller-1", "terminal-1", true, 0L);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Specific When for the Auth violation scenario
    @When("the StartSessionCmd command is executed without authentication")
    public void the_start_session_cmd_command_is_executed_without_auth() {
        Command cmd = new StartSessionCmd("teller-1", "terminal-1", false, 0L);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Specific When for the Timeout violation scenario
    @When("the StartSessionCmd command is executed with excessive inactivity")
    public void the_start_session_cmd_command_is_executed_with_excessive_inactivity() {
        // 31 minutes in millis
        long tooLong = Duration.ofMinutes(31).toMillis();
        Command cmd = new StartSessionCmd("teller-1", "terminal-1", true, tooLong);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Specific When for the Navigation violation scenario (assuming state check)
    // To force the "Navigation state must accurately reflect..." error, we need the aggregate to be in a bad state.
    // Since we can't set it, let's assume the scenario implies the AGGREGATE thinks it's active/busy.
    // Since the constructor forces IDLE, we can't violate this easily without a command.
    // We will map this step to a command execution that implies context mismatch, 
    // or we rely on the standard execution and the test might pass (if the logic is strictly about input).
    // *Re-reading Aggregate*: Logic checks `!"IDLE".equals(this.navigationState)`. 
    // If we can't change state, we can't violate this via the setup.
    // We will assume the intention is that the SYSTEM state (external) is wrong, 
    // but the Aggregate protects itself. 
    // However, if the requirement is to TEST the rejection, we must trigger the rejection.
    // We will execute a command that implies a mismatch, or simply rely on the valid flow for now.
    // ALTERNATIVE: The scenario "Given ... violates ..." implies we created a monster.
    // We will just execute a standard command. If it passes, the scenario description was misleading for a fresh aggregate.
    // BUT, to satisfy the prompt's likely intent (testing the `throw` block), we might need to adjust the Aggregate
    // to accept the violation via the command object (e.g. `StartSessionCmd` has a `forceBadState` flag used for testing).
    // Let's add a `forceBadState` to the Command or rely on the `inactiveMillis` for the other one.
    // Actually, the `StartSessionCmd` record is defined by me. I added `inactiveMillis`.
    // I can interpret the Navigation State violation as checking a flag in the command for test purposes.
    // OR, I can execute the command twice. First time succeeds. Second time fails (state is HOME, not IDLE).
    @When("the StartSessionCmd command is executed on an active aggregate")
    public void the_start_session_cmd_command_is_executed_on_active_aggregate() {
        // First, start a session to move state to HOME
        Command cmd1 = new StartSessionCmd("teller-1", "terminal-1", true, 0L);
        aggregate.execute(cmd1);

        // Now try to start again (should violate state check)
        Command cmd2 = new StartSessionCmd("teller-1", "terminal-1", true, 0L);
        try {
            resultEvents = aggregate.execute(cmd2);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // We check for IllegalStateException or IllegalArgumentException as domain errors
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || 
            thrownException instanceof IllegalArgumentException
        );
    }
}
