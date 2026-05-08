package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionState;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class S20Steps {

    // This repository is expected to be a mocked or in-memory implementation provided by the test context or spring context
    // For standalone Cucumber, we might instantiate it directly or rely on DI.
    // Assuming a simple mock/test-double pattern here.
    private final TellerSessionRepository repository = new com.example.domain.teller.repository.InMemoryTellerSessionRepository();

    private TellerSession aggregate;
    private Exception capturedException;
    private String currentSessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        currentSessionId = UUID.randomUUID().toString();
        // Create a valid active session
        aggregate = new TellerSession(currentSessionId);
        // Simulate a started session state directly via constructor or test helper.
        // Here we assume the constructor initializes state to ACTIVE/OPEN.
        // If we need to explicitly start it, we would execute a StartSessionCmd.
        // Based on the error log, we assume the aggregate exists.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in the previous step for simplicity, but we can assert here.
        Assertions.assertNotNull(currentSessionId);
    }

    // --- Negative Preconditions ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        currentSessionId = UUID.randomUUID().toString();
        aggregate = new TellerSession(currentSessionId);
        // Force state to unauthenticated/invalid
        aggregate.markState(TellerSessionState.UNAUTHENTICATED);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        currentSessionId = UUID.randomUUID().toString();
        aggregate = new TellerSession(currentSessionId);
        // Force state to timed out
        aggregate.markState(TellerSessionState.TIMED_OUT);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        currentSessionId = UUID.randomUUID().toString();
        aggregate = new TellerSession(currentSessionId);
        // Force state to an invalid navigation context
        aggregate.markState(TellerSessionState.NAVIGATION_ERROR);
    }

    // --- Action ---

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(currentSessionId);
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Should have emitted an event");
        Assertions.assertTrue(events.get(0) instanceof SessionEndedEvent, "Should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Should have thrown an exception");
        // Check type or message depending on strictness. DomainException or IllegalStateException is common.
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof DomainException);
    }
}
