package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.EndSessionCmd;
import com.example.domain.teller.model.SessionEndedEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-123");
        // Simulate a started session (authenticated)
        SessionStartedEvent startEvent = new SessionStartedEvent("SESSION-123", "TELLER-1", "TILL-05", Instant.now());
        // Since we don't have a public 'apply' for hydration in this simple snippet, 
        // we assume the repository handles loading, or we create a 'valid' state by using a factory method if it existed.
        // For the purpose of the test, we can instantiate and assume the 'valid' state logic is handled by the command execution context 
        // or we execute a command that puts it in a valid state.
        // However, to keep it simple and aligned with the prompt's structure, we'll assume the aggregate is hydrated correctly 
        // or we just test the command handling. 
        // To make "execute" pass authentication checks, the aggregate needs to know it is authenticated.
        // We will add a method to the Aggregate to hydrate it for testing purposes (omitted in prod, but standard for unit tests).
        aggregate.hydrateForTest("TELLER-1", "TILL-05", Instant.now(), null, true, false);
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The sessionId is implicitly provided by the aggregate instance ID in this test context
        assertNotNull(aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(thrownException, "Should not throw exception: " + (thrownException != null ? thrownException.getMessage() : ""));
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-INVALID");
        // Not authenticated (tellerId is null or empty)
        aggregate.hydrateForTest(null, null, null, null, false, false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Check for specific domain error types if necessary, e.g. IllegalStateException or a custom DomainException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEDOUT");
        // Simulate a session that started a long time ago
        aggregate.hydrateForTest("TELLER-1", "TILL-05", Instant.now().minus(Duration.ofHours(2)), null, true, false);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-BAD-NAV");
        // Simulate a session in a state where logging out is not allowed (e.g. mid-transaction)
        // We'll encode this as a 'locked' or 'busy' state in the aggregate for the test
        aggregate.hydrateForTest("TELLER-1", "TILL-05", Instant.now(), "TRANSACTION_IN_PROGRESS", true, false);
    }
}
