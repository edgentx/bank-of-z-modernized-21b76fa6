package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import com.example.domain.uimodel.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String tellerId;
    private String terminalId;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "TS-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Default valid state: authenticated and valid context
        this.aggregate.markAuthenticated(true);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "TS-FAIL-AUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly leaving authenticated as false (default)
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "TS-FAIL-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate stale auth context
        this.aggregate.setStaleActivity();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        this.sessionId = "TS-FAIL-NAV";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate being deep in a transaction flow already
        this.aggregate.setInvalidNavigationState("TRANSACTION_IN_PROGRESS");
        repository.save(aggregate);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "TELLER-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "TERM-3270-01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Reload from repository to ensure persistence logic flow
        TellerSessionAggregate agg = repository.findById(sessionId);
        Command cmd = new StartSessionCmd(sessionId, tellerId, terminalId);
        
        try {
            resultEvents = agg.execute(cmd);
            // If successful, persist the changes (events applied in aggregate)
            repository.save(agg);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof SessionStartedEvent);
        
        SessionStartedEvent startedEvent = (SessionStartedEvent) event;
        assertEquals("session.started", startedEvent.type());
        assertEquals(sessionId, startedEvent.aggregateId());
        assertEquals(tellerId, startedEvent.tellerId());
        assertEquals(terminalId, startedEvent.terminalId());
        assertTrue(startedEvent.occurredAt().isBefore(Instant.now()) || startedEvent.occurredAt().equals(Instant.now()));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
