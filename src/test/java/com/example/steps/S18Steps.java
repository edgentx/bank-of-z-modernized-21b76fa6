package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

/**
 * Cucumber Steps for S-18: Implement StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Constants for valid inputs
    private static final String VALID_TELLER_ID = "TELLER_001";
    private static final String VALID_TERMINAL_ID = "TERM_A01";
    private static final String VALID_NAV_STATE = "OPERATIONAL";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = repository.create("SESSION_123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // No-op, logic handled in When step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // No-op, logic handled in When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
            aggregate.id(),
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            true, // authenticated
            false, // not timed out
            VALID_NAV_STATE
        );
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION_AUTH_FAIL");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_TIMEOUT_FAIL");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION_NAV_FAIL");
    }

    // Custom When methods for negative scenarios to inject violation flags
    @When("the StartSessionCmd command is executed with unauthenticated teller")
    public void the_StartSessionCmd_command_is_executed_unauthenticated() {
        executeCommandWithParams(
            aggregate.id(),
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            false, // Not Authenticated
            false,
            VALID_NAV_STATE
        );
    }

    @When("the StartSessionCmd command is executed with timed out session")
    public void the_StartSessionCmd_command_is_executed_timed_out() {
        executeCommandWithParams(
            aggregate.id(),
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            true,
            true, // Timed Out
            VALID_NAV_STATE
        );
    }

    @When("the StartSessionCmd command is executed with invalid navigation state")
    public void the_StartSessionCmd_command_is_executed_invalid_navigation() {
        executeCommandWithParams(
            aggregate.id(),
            VALID_TELLER_ID,
            VALID_TERMINAL_ID,
            true,
            false,
            "INVALID_STATE"
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // We expect IllegalStateException based on our aggregate implementation
        Assertions.assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException, got: " + caughtException.getClass().getSimpleName());
    }

    private void executeCommandWithParams(String id, String tId, String termId, boolean isAuth, boolean isTimeout, String navState) {
        StartSessionCmd cmd = new StartSessionCmd(
            id,
            tId,
            termId,
            isAuth,
            isTimeout,
            navState
        );
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // In-memory implementation for test scope
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        @Override
        public TellerSessionAggregate save(TellerSessionAggregate aggregate) {
            return aggregate;
        }

        @Override
        public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.empty();
        }

        @Override
        public TellerSessionAggregate create(String id) {
            return new TellerSessionAggregate(id);
        }
    }
}
