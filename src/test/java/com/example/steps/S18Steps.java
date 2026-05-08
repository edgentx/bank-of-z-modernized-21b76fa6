package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-001");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-401");
        aggregate.setAuthenticated(false); // Simulate unauthenticated state
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-408");
        aggregate.setTimeoutConfig(Duration.ofSeconds(30)); // Very short timeout
        aggregate.setLastActivity(Instant.now().minusSeconds(60)); // Active long ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-500");
        aggregate.setNavigationStateCorrupted(true); // Force a failure in nav validation
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context handled in 'When' via command construction
    }

    @Given("a valid terminalId is provided")
    public void a valid_terminal_id_is_provided() {
        // Context handled in 'When' via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // We use valid defaults for the "happy path" data in the command
            Command cmd = new StartSessionCmd("SESSION-ID", "TELLER-101", "TERM-01");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull("Result events should not be null", resultEvents);
        assertFalse("Result events should not be empty", resultEvents.isEmpty());
        assertTrue("Expected SessionStartedEvent", resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull("Expected an exception to be thrown", caughtException);
        assertTrue("Expected IllegalArgumentException or IllegalStateException", 
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
