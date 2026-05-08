package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Create a valid, authenticated, active session
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate state by directly invoking state setters (testing helper)
        // or applying a startup command if it existed. Since we are testing 'EndSession'
        // and preconditions are complex, we hydrate the state directly for the test context.
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setSessionTimeout(Duration.ofMinutes(30));
        aggregate.setTerminalId("T-101");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The ID is implicitly provided by the aggregate instance used
        assertNotNull(aggregate.id());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.setAuthenticated(false); // Violation: not authenticated
        aggregate.setActive(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setSessionTimeout(Duration.ofMinutes(15));
        // Set last activity to 1 hour ago
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(1))); // Violation: timed out
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-500");
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        // Violation: Operating in a context that is inconsistent (e.g. Locked)
        aggregate.setNavigationState("LOCKED_DOWN"); 
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown an exception");
        // We expect a specific domain exception (IllegalStateException usually)
        assertTrue(capturedException instanceof IllegalStateException, "Expected domain error (IllegalStateException)");
    }
}
