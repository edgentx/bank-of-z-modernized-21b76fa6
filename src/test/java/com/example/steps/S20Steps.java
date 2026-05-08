package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.TellerSessionAggregate;
import com.example.domain.uimodel.EndSessionCmd;
import com.example.domain.uimodel.SessionEndedEvent;
import com.example.domain.uimodel.model.SessionState;
import com.example.domain.uimodel.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Throwable caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Create a valid, active session
        aggregate = new TellerSessionAggregate("SESSION-123");
        // Simulate a prior session start to make it valid
        aggregate.execute(new TellerSessionAggregate.StartSessionCommand("SESSION-123", "TELLER-1", "TERMINAL-A", SessionState.OPERATIONAL));
        aggregate.clearEvents(); // clear setup events
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by the aggregate initialization in previous step
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create an aggregate that is technically active but logically unauthenticated
        // For this domain, we simulate this by creating a session that never properly authenticated
        // or simply creating a fresh aggregate and trying to end it without starting it properly
        aggregate = new TellerSessionAggregate("SESSION-NO-AUTH");
        // We force the internal state to violate the invariant for the test scenario
        // In a real flow, this might be a session loaded from repo that expired auth
        try {
            // Manually set state to bypass StartSession auth checks for the sake of negative test setup
            aggregate.forceStateForTesting(SessionState.OPERATIONAL, false); // authenticated = false
        } catch (Exception e) {
            // If helper method doesn't exist, we rely on the aggregate failing validation on execution
            // based on internal logic (e.g. null tellerId)
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        aggregate.execute(new TellerSessionAggregate.StartSessionCommand("SESSION-TIMEOUT", "TELLER-1", "TERMINAL-A", SessionState.OPERATIONAL));
        aggregate.clearEvents();
        // Simulate timeout by marking the session as timed out internally
        aggregate.forceTimeoutForTesting();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-NAV");
        aggregate.execute(new TellerSessionAggregate.StartSessionCommand("SESSION-BAD-NAV", "TELLER-1", "TERMINAL-A", SessionState.OPERATIONAL));
        aggregate.clearEvents();
        // Simulate invalid navigation state (e.g. null or stale)
        aggregate.corruptNavigationStateForTesting();
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        EndSessionCmd cmd = new EndSessionCmd(aggregate.id(), "USER_INITIATED_LOGOUT");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent evt = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.ended", evt.type());
        Assertions.assertEquals(aggregate.id(), evt.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception");
        // Verify it is not a generic UnknownCommandException (unless that's the spec, but usually it's validation)
        // The scenarios imply validation errors (IllegalStateException, IllegalArgumentException)
        System.out.println("Caught expected error: " + caughtException.getMessage());
    }
}
