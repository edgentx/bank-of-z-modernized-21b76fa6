package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private StartSessionCmd validCmd;

    // Helper to build a valid command base
    private StartSessionCmd createValidCommand() {
        return new StartSessionCmd(
                "teller-123",
                "term-ABC",
                true, // authenticated
                Instant.now(), // recent activity
                "READY" // valid nav context
        );
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        validCmd = createValidCommand();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled by createValidCommand in the 'Given aggregate' step
        assertNotNull(validCmd.tellerId());
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled by createValidCommand in the 'Given aggregate' step
        assertNotNull(validCmd.terminalId());
    }

    @And("a valid tellerId is provided")
    public void and_a_valid_tellerId_is_provided() {
        // Alias for scenario flow
        a_valid_tellerId_is_provided();
    }

    @And("a valid terminalId is provided")
    public void and_a_valid_terminalId_is_provided() {
        // Alias for scenario flow
        a_valid_terminalId_is_provided();
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(validCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-1", event.aggregateId());
        assertEquals("teller-123", event.tellerId());
        assertEquals("term-ABC", event.terminalId());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // Create a command that reports false authentication
        validCmd = new StartSessionCmd(
                "teller-123",
                "term-ABC",
                false, // NOT authenticated
                Instant.now(),
                "READY"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        // Create a command with stale activity timestamp (e.g., 20 mins ago)
        // The aggregate default timeout is 15 mins.
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(20));
        validCmd = new StartSessionCmd(
                "teller-123",
                "term-ABC",
                true,
                oldTime, // Too old
                "READY"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_context() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        // Create a command with invalid nav context (e.g., null or "BUSY")
        validCmd = new StartSessionCmd(
                "teller-123",
                "term-ABC",
                true,
                Instant.now(),
                "BUSY" // Invalid context
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We expect an IllegalStateException or IllegalArgumentException based on domain logic
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
        assertNull(resultEvents, "No events should be emitted on failure");
    }
}
