package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSession aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Simple in-memory repo implementation for testing
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSession load(String id) {
            return null; // Not used in this specific test flow yet
        }

        @Override
        public void save(TellerSession aggregate) {
            // No-op for test
        }
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSession("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Context setup, handled in When step via Command
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Context setup, handled in When step via Command
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSession("session-violate-auth");
        // This aggregate instance will be constructed in a way that it cannot accept a command
        // or we mock the internal state if possible. Given the constructor, we rely on command validation.
        // However, the requirement implies the aggregate's state might be invalid or the command is invalid.
        // Here we simulate a pre-existing session state where start is invalid.
        aggregate.markAsStarted(); // Simulate state where StartSessionCmd is invalid
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        // This scenario implies we are testing an invariant check during command execution.
        // For the purpose of this unit test, we assume the command checks these constraints.
        aggregate = new TellerSession("session-timeout");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        // This scenario implies a validation check on the navigation context passed via command.
        aggregate = new TellerSession("session-nav-error");
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Using standard valid data for the happy path or specific violation contexts
            // For violation tests, we might pass specific invalid data or rely on the aggregate state prepared in Given.
            // The Prompt implies the aggregate state violates the rule, so we send a standard command.
            Command cmd = new StartSessionCmd(aggregate.id(), "teller-1", "terminal-1");
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // In DDD, domain errors are often exceptions thrown from the Aggregate
    }
}
