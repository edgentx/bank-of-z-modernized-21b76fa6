package com.example.steps;

import com.example.domain.shared.*;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.List;

public class S18Steps {
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("TS-1");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context handled in the 'When' step via command construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Assume valid IDs for the positive scenario
            StartSessionCmd cmd = new StartSessionCmd("TS-1", "user-1", "term-1", true, "+", 0, 0);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("TS-1", event.aggregateId());
        assertEquals("session.started", event.type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("TS-2");
    }

    @When("the StartSessionCmd command is executed on unauthenticated session")
    public void the_command_is_executed_on_unauthenticated_session() {
        try {
            // isAuthenticated = false
            StartSessionCmd cmd = new StartSessionCmd("TS-2", "user-1", "term-1", false, "+", 0, 0);
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("TS-3");
        // Force state to be SESSION_STARTED but set a lastActiveTime far in the past
        aggregate.testInjectState("SESSION_STARTED", Instant.now().minusSeconds(3600));
    }

    @When("the StartSessionCmd command is executed on timed out session")
    public void the_command_is_executed_on_timed_out_session() {
        try {
            StartSessionCmd cmd = new StartSessionCmd("TS-3", "user-1", "term-1", true, "+", 0, 0);
            aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            caughtException = e;
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("TS-4");
        // Force state to be inconsistent, e.g. SESSION_STARTED but navState invalid
        aggregate.testInjectState("SESSION_STARTED", Instant.now());
    }

    @When("the StartSessionCmd command is executed on invalid nav state")
    public void the_command_is_executed_on_invalid_nav_state() {
        try {
            // Passing invalid function keys/context to trigger validation error
            StartSessionCmd cmd = new StartSessionCmd("TS-4", "user-1", "term-1", true, "INVALID", 0, 0);
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}