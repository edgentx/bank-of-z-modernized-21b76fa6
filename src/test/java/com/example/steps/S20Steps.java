package com.example.steps;

import com.example.domain.navigation.model.EndSessionCmd;
import com.example.domain.navigation.model.SessionEndedEvent;
import com.example.domain.navigation.model.TellerSession;
import com.example.domain.navigation.repository.TellerSessionRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    // Using an in-memory repository as per requirements (no DB)
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private TellerSession store;
        @Override
        public TellerSession save(TellerSession aggregate) {
            this.store = aggregate;
            return aggregate;
        }
        @Override
        public Optional<TellerSession> findById(String id) {
            return Optional.ofNullable(store);
        }
        @Override
        public void deleteById(String id) {
            this.store = null;
        }
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("session-123");
        aggregate.markAuthenticated("teller-01");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled implicitly by the aggregate initialization
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSession("session-invalid-auth");
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-timeout");
        aggregate.markAuthenticated("teller-01");
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = new TellerSession("session-bad-nav");
        aggregate.markInactive();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id(), "teller-01", Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertFalse(aggregate.isActive()); // Verify state mutation
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}