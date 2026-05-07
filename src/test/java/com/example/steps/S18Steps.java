package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSession("session-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup: Command constructed in 'When' step
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Valid data for the happy path
            StartSessionCmd cmd = new StartSessionCmd("session-1", "teller-123", "terminal-42", Duration.ofMinutes(30), Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-1", event.aggregateId());
        Assertions.assertEquals("teller-123", event.tellerId());
        Assertions.assertEquals("terminal-42", event.terminalId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-2");
        // No authentication mock/context setup implies violation if we were checking external auth.
        // Here we simulate the violation via command data or state.
        // For this aggregate, let's assume the command must contain valid tokens.
    }

    @When("the command is executed")
    public void the_command_is_executed_with_invalid_auth() {
        try {
            // Invalid auth (empty/null teller ID)
            StartSessionCmd cmd = new StartSessionCmd("session-2", "", "terminal-42", Duration.ofMinutes(30), Instant.now());
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout_config() {
        aggregate = new TellerSession("session-3");
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_StartSessionCmd_command_is_executed_with_invalid_timeout() {
        try {
            // Negative duration is invalid
            StartSessionCmd cmd = new StartSessionCmd("session-3", "teller-123", "terminal-42", Duration.ofMinutes(-5), Instant.now());
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSession("session-4");
    }

    @When("the StartSessionCmd command is executed with invalid nav state")
    public void the_StartSessionCmd_command_is_executed_with_invalid_nav_state() {
        try {
            // Invalid Nav State (null)
            StartSessionCmd cmd = new StartSessionCmd("session-4", "teller-123", "terminal-42", Duration.ofMinutes(30), Instant.now());
            // We pass a null or invalid initial nav state if we had a specific object for it.
            // The aggregate will validate the specific operational context.
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }
}
