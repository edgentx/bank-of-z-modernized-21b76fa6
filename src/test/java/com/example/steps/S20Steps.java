package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermemory.model.*;
import com.example.domain.tellermemory.repository.TellerSessionRepository;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/S-20.feature")
public class S20Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-123");
        // Simulate a previously started valid session
        aggregate.loadFromHistory(List.of(
            new SessionStartedEvent("SESSION-123", "TELLER-1", Instant.now())
        ));
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate ID setup in previous step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-404");
        // No events loaded -> Session doesn't exist / not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(31)); // > 30 min timeout
        aggregate.loadFromHistory(List.of(
            new SessionStartedEvent("SESSION-TIMEOUT", "TELLER-1", oldTime)
        ));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-NAV");
        aggregate.loadFromHistory(List.of(
            new SessionStartedEvent("SESSION-BAD-NAV", "TELLER-1", Instant.now())
        ));
        // Simulate an invariant check where the aggregate knows it's in a bad state
        // In a real app, this might be set by a previous command. Here we mock the internal state directly.
        // We'll use a specific setter or internal logic check if available, otherwise we check behavior.
        // For this test, we expect the aggregate to throw if we don't handle the context.
        // The TellerSessionAggregate will have logic to verify currentScreen vs expectedContext.
        aggregate.markNavigationInvalid("Context mismatch: Expected MAIN_MENU, found WITHDRAWAL");
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        Command cmd = new EndSessionCmd("SESSION-123", "User logout");
        try {
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
        assertEquals("session.ended", resultEvents.get(0).type());
        assertEquals("SESSION-123", resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNull(resultEvents); // No events should be emitted
        assertNotNull(caughtException);
        // In DDD, invariants are often enforced via IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException ||
                   caughtException instanceof UnknownCommandException);
        System.out.println("Caught expected error: " + caughtException.getMessage());
    }
}
