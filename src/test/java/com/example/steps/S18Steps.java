package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Cucumber Steps for S-18: Implement StartSessionCmd on TellerSession.
 */
public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // By default, make it authenticated so the success case works
        aggregate.markAuthenticated();
        aggregate.setNavigationContext("IDLE");
        aggregate.setLastActivityAt(Instant.now()); // Recent activity
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Command is created in the 'When' step, this is just documentation
        // or we can store the tellerId if needed.
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Same as above.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        // isAuthenticated defaults to false in constructor, but let's be explicit.
        // aggregate.markAuthenticated() is NOT called.
        aggregate.setNavigationContext("IDLE");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        aggregate.setNavigationContext("IDLE");
        // Set activity to 2 hours ago (assuming default timeout is 30 mins)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // Set context to something other than IDLE (e.g. mid-transaction)
        aggregate.setNavigationContext("TRANSACTION_IN_PROGRESS");
    }

    // --- Whens ---

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        // Create a valid command payload
        command = new StartSessionCmd(aggregate.id(), "teller-01", "terminal-01");
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("teller-01", event.tellerId());
        Assertions.assertEquals("terminal-01", event.terminalId());
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // We expect IllegalStateException for domain invariant violations in this pattern
        Assertions.assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException, got " + caughtException.getClass().getSimpleName());
    }
}
