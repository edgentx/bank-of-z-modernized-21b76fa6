package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Constants for testing
    private static final String SESSION_ID = "sess-123";
    private static final String TELLER_ID = "teller-01";
    private static final String TERMINAL_ID = "term-42";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup state to satisfy success path preconditions if necessary
        // Assuming standard flow requires authentication state before starting command
        aggregate.setAuthenticated(true);
        aggregate.setNavigationContext("AUTHENTICATED");
        repository.save(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Placeholder for data setup, used in the 'When' step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Placeholder for data setup, used in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setAuthenticated(false); // Violation
        aggregate.setNavigationContext("LOGIN");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        // Set last activity to 20 minutes ago to simulate timeout (Assuming timeout is 15m)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200)); 
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.setAuthenticated(true);
        aggregate.setNavigationContext("LOCKED"); // Invalid context for starting
        repository.save(aggregate);
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(SESSION_ID, TELLER_ID, TERMINAL_ID);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(TELLER_ID, event.tellerId());
        assertEquals(TERMINAL_ID, event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on specific invariant enforcement, this could be IllegalStateException or others
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
