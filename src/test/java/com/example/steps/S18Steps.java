package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.ui.model.SessionStartedEvent;
import com.example.domain.ui.model.StartSessionCmd;
import com.example.domain.ui.model.TellerSessionAggregate;
import com.example.domain.ui.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-18: TellerSession.
 */
public class S18Steps {

    private TellerSessionRepository repository;
    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> result;
    private Exception caughtException;

    // Standard timeout configuration: 15 minutes
    private static final Duration TIMEOUT = Duration.ofMinutes(15);

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        repository = new TellerSessionRepository(TIMEOUT);
        aggregate = repository.create();
        aggregate.markAuthenticated(); // Pre-condition for valid start
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Teller ID provided in When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Terminal ID provided in When step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        repository = new TellerSessionRepository(TIMEOUT);
        aggregate = repository.create();
        aggregate.markUnauthenticated(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        repository = new TellerSessionRepository(TIMEOUT);
        aggregate = repository.create();
        aggregate.markAuthenticated();
        aggregate.markSessionStale(); // Violation
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        repository = new TellerSessionRepository(TIMEOUT);
        aggregate = repository.create();
        aggregate.markAuthenticated();
        aggregate.markNavigationContextLocked(); // Violation
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(aggregate.id(), "teller-123", "term-456");
            result = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("session.started", result.get(0).type());
        assertTrue(result.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verify the error message matches the invariant
        assertTrue(caughtException.getMessage().contains("must") || 
                   caughtException.getMessage().contains("Navigation state"));
    }
}
