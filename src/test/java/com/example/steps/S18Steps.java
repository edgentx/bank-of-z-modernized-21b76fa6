package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSession;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private Throwable thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSession("session-123");
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Placeholder - context set in When
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Placeholder - context set in When
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
                "teller-01",
                "terminal-05",
                true,
                Instant.now(),
                "MAIN_MENU"
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Throwable e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Expected no exception, but got: " + thrownException.getMessage());
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-01", event.tellerId());
        assertEquals("terminal-05", event.terminalId());
        assertEquals("MAIN_MENU", event.initialContext());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-timeout-fail");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSession("session-nav-fail");
    }

    @When("the StartSessionCmd command is executed with violations")
    public void the_StartSessionCmd_command_is_executed_with_violations() {
        // Determine failure type based on ID to avoid complex state management in steps
        String id = aggregate.id();
        StartSessionCmd cmd = null;

        if (id.contains("auth-fail")) {
            cmd = new StartSessionCmd("t-01", "term-01", false, Instant.now(), "MAIN_MENU");
        } else if (id.contains("timeout-fail")) {
            // Create a timestamp in the past to simulate stale auth/token
            cmd = new StartSessionCmd("t-01", "term-01", true, Instant.now().minusSeconds(3600), "MAIN_MENU");
        } else if (id.contains("nav-fail")) {
            // Invalid context
            cmd = new StartSessionCmd("t-01", "term-01", true, Instant.now(), "");
        }

        try {
            aggregate.execute(cmd);
        } catch (Throwable e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected a domain error exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException, "Expected IllegalArgumentException");
    }
}
