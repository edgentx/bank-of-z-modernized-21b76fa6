package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private static final String SESSION_ID = "session-123";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(); // Ensure it meets auth invariant
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is already set in the previous step
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd(SESSION_ID);
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals("session.ended", event.type());
    }

    // Error Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // By default, markAuthenticated is NOT called, so isAuthenticated is false.
        // But we might need to ensure the "valid" state aside from auth if needed. 
        // In the current implementation, the constructor sets isAuthenticated = false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(); // Ensure auth passes so we hit timeout check
        aggregate.markTimedOut();      // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(); // Ensure auth passes
        aggregate.setBusyState();      // Force non-idle state (BUSY)
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
