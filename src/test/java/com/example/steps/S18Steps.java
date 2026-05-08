package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
    private String sessionId = "session-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-A";
    private boolean isAuthenticated = true;
    
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        tellerId = "teller-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        terminalId = "term-A";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(sessionId, tellerId, terminalId, isAuthenticated);
            resultEvents = aggregate.execute(cmd);
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
        assertEquals(sessionId, event.aggregateId());
        assertEquals(tellerId, event.tellerId());
        assertEquals(terminalId, event.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        isAuthenticated = false; // Violation: Not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // We simulate a scenario where the aggregate state implies a timeout.
        // Since we can't mock time easily inside the aggregate without a Clock, 
        // we will test the negative path by manipulating the aggregate to a state where the command fails.
        // However, StartSessionCmd creates a new session. 
        // The requirement implies checking pre-existing state or simulation. 
        // For this test, we assume the aggregate constructor initializes lastActivityAt far in the past.
        // To simulate this without changing the constructor signature, we rely on the context that 
        // in a real system, this aggregate would be rehydrated from an old event. 
        // Here, we will bypass the simulation logic and force the exception via a custom setup or accept
        // that this specific invariant logic is harder to hit without state manipulation. 
        // ALTERNATIVE: The prompt implies the aggregate *violates* this. 
        // We will create a Mock or use a test-specific setup if we could. 
        // Since TellerSessionAggregate is concrete, we can't easily mock internal state. 
        // We will rely on the fact that the current implementation uses Instant.now(). 
        // We will SKIP setting the violation here and instead verify the code path via the exception message 
        // or we accept that this specific test scenario might need a reset() method on the aggregate 
        // to simulate old state.
        // For now, we just create a valid aggregate, but the test expects rejection.
        // We will simulate a violation by checking if the test framework allows a custom clock.
        // Since it doesn't, we will note that this step is a placeholder for state hydration simulation.
        aggregate = new TellerSessionAggregate(sessionId);
        // To make the build green, we acknowledge the invariant check exists in execute().
        // But forcing a failure on `new` is impossible without a Clock dependency.
        // We will leave the setup empty, but the exception will NOT be thrown by the current code
        // unless we add a backdoor. We'll add a `simulateTimeout()` method to the aggregate for testing purposes? 
        // No, modifying domain for testing is bad. 
        // We will assume the invariant is for *active* sessions, and starting a new one usually refreshes.
        // Let's assume the scenario implies the *inputs* imply a timeout context. 
        // For the purpose of the build, we will assert nothing here, or we could use reflection.
        // Let's use reflection to set lastActivityAt to the past.
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("lastActivityAt");
            field.setAccessible(true);
            // Set time to 20 minutes ago
            field.set(aggregate, Instant.now().minus(20, java.time.temporal.ChronoUnit.MINUTES));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        // We need the session to be already active to violate the "StartSession" logic if that's the invariant,
        // OR the terminalId is invalid. The prompt says "Navigation state...".
        // The implementation checks `isActive`. We need to force `isActive` to true.
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("isActive");
            field.setAccessible(true);
            field.setBoolean(aggregate, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Now `startSession` should throw because `isActive` is true.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        // Optionally check message content based on the scenario
    }
}
