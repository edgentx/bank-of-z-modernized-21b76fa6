package com.example.steps;

import com.example.domain.navigation.model.EndSessionCmd;
import com.example.domain.navigation.model.SessionEndedEvent;
import com.example.domain.navigation.model.TellerSessionAggregate;
import com.example.domain.navigation.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<SessionEndedEvent> result;
    private Exception exception;

    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private TellerSessionAggregate store;
        @Override public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            this.store = aggregate;
            return aggregate;
        }
        @Override public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.ofNullable(store);
        }
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-1");
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID implicitly handled by aggregate initialization in the previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally not calling markAuthenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-1");
        aggregate.markTimedOut();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-invalid");
        aggregate.markAuthenticated("teller-1");
        aggregate.markNavigationInvalid();
        repository.save(aggregate);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            var cmd = new EndSessionCmd(aggregate.id());
            result = (List<SessionEndedEvent>) aggregate.execute(cmd);
        } catch (Exception e) {
            exception = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("session.ended", result.get(0).type());
        assertEquals(aggregate.id(), result.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(exception);
        assertTrue(exception instanceof IllegalStateException);
    }
}
