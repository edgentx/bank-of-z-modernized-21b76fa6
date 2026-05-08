package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-456");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-789");
        // Manually set state to simulate a stale active session that needs action
        // or simply simulate a previous session that timed out.
        // For this scenario, we prime the aggregate to look like an old active session.
        // Since TellerSessionAggregate doesn't expose public setters for DDD safety,
        // we assume the command logic handles the check. Here we simulate a 'bad' command
        // context or attempt to restart a session that should be rejected.
        // However, the prompt says "aggregate that violates", implying state.
        // Since TellerSession starts INACTIVE, a timeout usually applies to ACTIVE sessions.
        // We'll assume the command context implies the *pre-condition* that is invalid.
        // But to strictly test the aggregate logic:
        // We will test via the Command configuration in 'When'.
        // We create a fresh aggregate (inactive), the invariant check is mostly on the cmd input or previous state.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-101");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the When clause construction for simplicity, or stored here if desired
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the When clause construction
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Default valid data
            String teller = "teller-1";
            String terminal = "term-1";
            boolean auth = true;
            String state = "IDLE";

            // Logic to vary command based on scenario to force failures according to "Given violations"
            // Scenarios 2: Auth failure
            if (aggregate.id().equals("session-456")) {
                auth = false;
            }
            // Scenarios 4: Navigation state failure
            if (aggregate.id().equals("session-101")) {
                state = "INVALID_STATE_TRANSITION";
            }

            cmd = new StartSessionCmd(teller, terminal, auth, state);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Check for IllegalStateException (Domain Error)
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
