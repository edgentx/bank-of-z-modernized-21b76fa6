package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSession aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSession("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the When step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a valid_terminalId_is_provided() {
        // Handled in the When step via command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-auth-fail");
        aggregate.markAsUnauthenticated(); // Setup state to fail check
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        // For this scenario, we interpret the invariant as ensuring the command isn't stale
        // or invalid data is passed. The aggregate validates the command inputs.
        aggregate = new TellerSession("session-timeout-fail");
        // The Command will carry a timestamp that is invalid (too old)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession("session-nav-fail");
        // The Command will carry an invalid state
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            Command cmd;
            String id = aggregate.id();
            
            if (id.contains("auth-fail")) {
                cmd = new StartSessionCmd("teller-1", "terminal-1", null, java.time.Instant.now());
            } else if (id.contains("timeout-fail")) {
                // Timestamp too old
                cmd = new StartSessionCmd("teller-1", "terminal-1", "INIT", java.time.Instant.now().minusSeconds(3600));
            } else if (id.contains("nav-fail")) {
                // Invalid state
                cmd = new StartSessionCmd("teller-1", "terminal-1", "INVALID_STATE", java.time.Instant.now());
            } else {
                // Valid command
                cmd = new StartSessionCmd("teller-1", "terminal-1", "INIT", java.time.Instant.now());
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected exception but none was thrown");
        // In a real app, might catch a specific DomainException, here we check for IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }
}
