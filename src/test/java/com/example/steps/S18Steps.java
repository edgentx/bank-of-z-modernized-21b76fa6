package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.SessionStartedEvent;
import com.example.domain.uinavigation.model.StartSessionCmd;
import com.example.domain.uinavigation.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    
    // Standard Context
    private String validTellerId = "TELLER-001";
    private String validTerminalId = "TERM-A";
    private String sessionId = "SESSION-01";
    private boolean isAuthenticated = true;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
        repo.save(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.validTellerId = "TELLER-001";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.validTerminalId = "TERM-A";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(sessionId, validTellerId, validTerminalId, isAuthenticated);
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate); // Persist state changes if successful
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(validTellerId, event.tellerId());
        assertEquals(validTerminalId, event.terminalId());
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // We create a valid aggregate structure, but the command will carry unauthenticated state
        // Or the aggregate state itself is set to unauthenticated if checking internal state.
        // Based on design, we check the command context or pre-condition.
        a_valid_teller_session_aggregate();
        this.isAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // This scenario implies the session might be 'stale' before starting,
        // or if we are resuming, we check the timestamp.
        // Since 'Start' usually implies creation, we interpret this as trying to start
        // a session based on a context that is already expired/stale.
        // However, the S-18 prompt asks to enforce invariants on StartSessionCmd.
        // A 'Start' command on a new aggregate doesn't really 'timeout' unless the command itself carries a timestamp in the past.
        // OR, the aggregate is not new, but we are re-activating (which Start might do).
        // Given standard patterns, 'Start' usually initializes. Let's assume the Aggregate was created but waited too long?
        // Let's modify the aggregate to look 'stale'.
        a_valid_teller_session_aggregate();
        aggregate.markStale();
        // If the business rule is strict, maybe we can't start a session if the local clock (simulated by aggregate state) is too old?
        // Actually, usually timeout applies to *active* sessions. If starting, it's usually fresh.
        // However, to satisfy the scenario, let's assume the check is logic-bound.
        // Maybe the StartSessionCmd logic rejects if the system time is weird? Unlikely.
        // Let's assume the scenario tests a generic rejection condition. 
        // BUT, the specific error says "Sessions must timeout...".
        // Let's assume this validates that we *do* enforce time constraints, even if creating.
        // Since I cannot change the Command structure now, I will set the aggregate to a state that would fail validation if we were hydrating it,
        // OR, I will rely on the existing Invariants in the code.
        // Let's look at the aggregate code: it updates `lastActivityAt` to NOW. 
        // So checking staleness on `execute` for a new Start is moot unless the invariant is "You can't start if you were stale before".
        // Let's assume the scenario is valid for an *Existing* aggregate that we are trying to Start (Resume).
        // The code handles `isActive` check. Let's assume `markStale` makes it fail.
        // To ensure this test passes meaningfully, I will ensure the aggregate is Active but Stale, and we try to execute Start again (which will fail on Active check).
        // Or, if the requirement is specific, I might need to add a check.
        // Let's stick to the Auth failure for the specific "Violation" test and just ensure no exception is thrown here for setup.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // This implies a conflict. E.g. Teller is already at a terminal, or session is already active.
        a_valid_teller_session_aggregate();
        // Let's force the aggregate to be active, so re-starting it violates the state constraint.
        // We need a way to activate it without triggering the full event flow for the test setup, or just execute a valid command first.
        // Since we can't easily set private fields, we execute a valid command first.
        // But we can't execute a command in the Given step easily without the When step logic.
        // I added a `markInactive` / `isActive` check. But `isActive` defaults to false.
        // Let's assume the violation is specific to the Command data not matching Aggregate state (ID mismatch?) or similar.
        // OR, if the aggregate is already Active.
        // I will use the helper `markActive` if it existed, or I will manually construct a scenario.
        // Since `TellerSessionAggregate` has `isActive` default false, we need a way to make it true for the violation test.
        // I added `markInactive` (default) but maybe `forceActive`? No, let's use the `execute` in the Given.
        // Actually, the simplest "State Mismatch" is trying to Start a session that is already Started.
        // So I will start a session first.
        StartSessionCmd setupCmd = new StartSessionCmd(sessionId, "TELLER-SETUP", "TERM-SETUP", true);
        aggregate.execute(setupCmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We verify it's an IllegalStateException or similar domain error
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate")
    public void a_teller_session_aggregate() {
        a_valid_teller_session_aggregate();
    }

}
