package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = repository.getOrCreate("session-123");
        aggregate.markAuthenticatedAndSynced("session-123");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the setup in 'a_valid_teller_session_aggregate'
        // The command will use the ID known to the aggregate.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = repository.getOrCreate("session-auth-fail");
        // We do NOT mark authenticated. The aggregate defaults to unauthenticated.
        // Ensure it is active-ish to isolate the auth failure.
        aggregate.markAuthenticatedAndSynced("session-auth-fail");
        aggregate.violateAuthentication();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = repository.getOrCreate("session-timeout");
        aggregate.markAuthenticatedAndSynced("session-timeout");
        aggregate.violateTimeout();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = repository.getOrCreate("session-nav-error");
        aggregate.markAuthenticatedAndSynced("session-nav-error");
        aggregate.violateNavigation();
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            var cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event must be SessionEndedEvent");
        assertNull(capturedException, "Should not have thrown an exception");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Exception should be thrown for domain violation");
        assertTrue(capturedException instanceof IllegalStateException, "Exception must be IllegalStateException (Domain Error)");
        assertNull(resultEvents || resultEvents.isEmpty(), "No events should be emitted on failure");
    }
}
