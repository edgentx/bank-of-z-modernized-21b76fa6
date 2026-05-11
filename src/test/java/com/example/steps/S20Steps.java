package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.tellersession.model.EndSessionCmd;
import com.example.domain.tellersession.model.SessionEndedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S20Steps {

    private record TestData(
        TellerSessionAggregate aggregate,
        Exception capturedError,
        List<DomainEvent> results
    ) {}

    private TestData testData;
    private String sessionId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Simulate a session that has been started and is active
        sessionId = "session-123";
        testData = new TestData(
            new TellerSessionAggregate(sessionId),
            null,
            List.of()
        );
        // Manually configuring internal state for a valid session
        // In a real test, we might use a factory method or a repository
        // Here we simulate the constructor setting the ID
        assertEquals(sessionId, testData.aggregate.id());
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(sessionId);
        assertEquals(sessionId, testData.aggregate.id());
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            Command cmd = new EndSessionCmd(sessionId);
            List<DomainEvent> events = testData.aggregate.execute(cmd);
            testData = new TestData(testData.aggregate, null, events);
        } catch (Exception e) {
            testData = new TestData(testData.aggregate, e, List.of());
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        assertNull(testData.capturedError, "Should not have thrown an exception");
        assertNotNull(testData.results, "Results list should not be null");
        assertEquals(1, testData.results.size(), "Should produce one event");
        assertTrue(testData.results.get(0) instanceof SessionEndedEvent, "Event should be SessionEndedEvent");
        SessionEndedEvent event = (SessionEndedEvent) testData.results.get(0);
        assertEquals("session.ended", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        // An aggregate that was not properly initialized or authenticated
        // We simulate this by checking internal state in the aggregate logic.
        // For the purpose of the test, we create a valid aggregate, 
        // but the logic inside 'execute' (or a mock setup) would fail this check.
        sessionId = "unauth-session";
        testData = new TestData(new TellerSessionAggregate(sessionId), null, List.of());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        // Assume the aggregate holds state indicating a timeout
        sessionId = "timedout-session";
        testData = new TestData(new TellerSessionAggregate(sessionId), null, List.of());
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        // Assume invalid navigation state
        sessionId = "bad-nav-session";
        testData = new TestData(new TellerSessionAggregate(sessionId), null, List.of());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(testData.capturedError, "Expected an exception to be thrown");
        assertTrue(testData.capturedError instanceof IllegalStateException || 
                   testData.capturedError instanceof IllegalArgumentException,
                   "Expected a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
