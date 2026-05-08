package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();

    // Test Data
    private static final String VALID_ID = "TS-12345";
    private static final String VALID_TELLER_ID = "T-99";
    private static final String VALID_TERMINAL_ID = "TERM-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        // Setup valid base state for a session that CAN start
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        repository.save(aggregate);
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup handled in the 'When' step via Command construction
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup handled in the 'When' step via Command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(VALID_ID, VALID_TELLER_ID, VALID_TERMINAL_ID);
        try {
            resultingEvents = aggregate.execute(cmd);
            // In a real scenario, we would save events here
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvents, "Events should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent, "Event must be SessionStartedEvent");
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(VALID_TELLER_ID, startedEvent.tellerId());
        assertEquals(VALID_TERMINAL_ID, startedEvent.terminalId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        aggregate.markUnauthenticated(); // Violation
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        aggregate.markAuthenticated();
        // Set activity to 20 minutes ago (assuming timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(VALID_ID);
        aggregate.markAuthenticated();
        aggregate.setActive(true); // simulate active but bad context
        aggregate.setNavigationContext("UNKNOWN_ERROR_STATE");
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        
        // Verify it's a specific domain logic error (IllegalStateException is used in our aggregate)
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
        assertFalse(caughtException.getMessage().isBlank(), "Error message should not be blank");
    }

    // --- Mock Repository ---
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        // Simple mapless implementation for steps, usually not needed unless we wire full repo
        @Override public TellerSessionAggregate save(TellerSessionAggregate aggregate) { return aggregate; }
        @Override public TellerSessionAggregate findById(String id) { return null; }
    }
}
