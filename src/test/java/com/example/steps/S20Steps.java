package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "ts-123";
        // Use a command to hydrate the aggregate to a valid state
        StartSessionCmd startCmd = new StartSessionCmd(
            id, 
            "teller-01", 
            "terminal-A", 
            Instant.now().minusSeconds(60), // Started 1 min ago
            Instant.now().plus(Duration.ofHours(1)) // Expires later
        );
        aggregate = new TellerSessionAggregate(id);
        aggregate.execute(startCmd);
        aggregate.clearEvents(); // Clear hydration events so we can test the End event cleanly
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID handled in aggregate construction
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // We simulate an aggregate that was somehow created without proper auth (or logged out)
        String id = "ts-no-auth";
        aggregate = new TellerSessionAggregate(id);
        // State remains uninitialized (null tellerId), which implies no authentication
        // based on the invariant logic we will write.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String id = "ts-timeout";
        StartSessionCmd startCmd = new StartSessionCmd(
            id, 
            "teller-01", 
            "terminal-A", 
            Instant.now().minus(Duration.ofHours(2)), // Started 2 hours ago
            Instant.now().minus(Duration.ofMinutes(1)) // Expired 1 min ago
        );
        aggregate = new TellerSessionAggregate(id);
        aggregate.execute(startCmd);
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String id = "ts-bad-nav";
        StartSessionCmd startCmd = new StartSessionCmd(
            id, 
            "teller-01", 
            "terminal-A", 
            Instant.now(),
            Instant.now().plusHours(1)
        );
        aggregate = new TellerSessionAggregate(id);
        aggregate.execute(startCmd);
        aggregate.clearEvents();
        // Manually corrupt the state via reflection or a setter if available to simulate 'out of sync' navigation
        // Since we are in the same package, we can assume we might have setVisibleState access, 
        // but for strict aggregate encapsulation, we might just be in a state where the 
        // command tells us to end, but the navigation stack is not empty (requires "Exit" actions first).
        // The invariant logic will check this.
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            EndSessionCmd cmd = new EndSessionCmd(aggregate.id());
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        List<DomainEvent> events = aggregate.uncommittedEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof SessionEndedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
