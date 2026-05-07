package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.EndSessionCmd;
import com.example.domain.uimodel.model.SessionEndedEvent;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private final String TEST_SESSION_ID = "session-123";
    private final String TEST_AUTH_ID = "teller-01";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        // Simulate prior session start to put aggregate in a valid active state
        aggregate.bootstrapSessionStarted(TEST_AUTH_ID, Instant.now().minusSeconds(60));
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The ID is implicit in the aggregate instance, verified via command consistency check
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        // Create aggregate but do not bootstrap with authentication state (null/empty auth)
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        // aggregate.authenticatedTellerId remains null
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        // Bootstrap with a timestamp older than the allowed timeout (e.g. 35 minutes ago)
        aggregate.bootstrapSessionStarted(TEST_AUTH_ID, Instant.now().minus(Duration.ofMinutes(35)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(TEST_SESSION_ID);
        aggregate.bootstrapSessionStarted(TEST_AUTH_ID, Instant.now());
        // Corrupt the state to simulate a pending transaction that requires completion
        aggregate.setSimulatedOpenTransactionCount(1);
    }

    @When("the EndSessionCmd command is executed")
    public void the_EndSessionCmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(TEST_SESSION_ID);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "One event should be emitted");
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent, "Event type must be SessionEndedEvent");
        Assertions.assertEquals("session.ended", resultEvents.get(0).type());
        Assertions.assertNull(capturedException, "No exception should have been thrown");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "An exception should have been thrown");
        // We verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Exception must be a domain error (IllegalStateException or IllegalArgumentException) but was: " + capturedException.getClass().getSimpleName()
        );
        Assertions.assertNull(resultEvents || resultEvents.isEmpty(), "No events should be emitted on rejection");
    }
}
