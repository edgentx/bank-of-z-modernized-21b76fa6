package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSession;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSession aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    private final String VALID_TELLER_ID = "teller-123";
    private final String VALID_TERMINAL_ID = "term-A";
    private final String VALID_NAV_CTX = "MAIN_MENU";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSession("session-abc");
        capturedException = null;
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // State implied for the next command execution
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // State implied
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, true, 900, VALID_NAV_CTX);
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);

        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session-abc", event.aggregateId());
        assertEquals(VALID_TELLER_ID, event.tellerId());
        assertEquals(VALID_TERMINAL_ID, event.terminalId());
        assertEquals(VALID_NAV_CTX, event.navigationContext());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSession("session-auth-fail");
        capturedException = null;
    }

    @When("the StartSessionCmd command is executed with invalid auth")
    public void the_start_session_cmd_command_is_executed_with_invalid_auth() {
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, false, 900, VALID_NAV_CTX);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSession("session-timeout-fail");
        capturedException = null;
    }

    @When("the StartSessionCmd command is executed with invalid timeout")
    public void the_start_session_cmd_command_is_executed_with_invalid_timeout() {
        // Testing timeout > MAX (1800)
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, true, 2000, VALID_NAV_CTX);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        aggregate = new TellerSession("session-nav-fail");
        capturedException = null;
    }

    @When("the StartSessionCmd command is executed with invalid navigation")
    public void the_start_session_cmd_command_is_executed_with_invalid_navigation() {
        // Testing blank context
        executeCommand(VALID_TELLER_ID, VALID_TERMINAL_ID, true, 900, " ");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException);
    }

    private void executeCommand(String tellerId, String terminalId, boolean authenticated, int timeout, String navCtx) {
        try {
            var cmd = new StartSessionCmd(
                    tellerId,
                    terminalId,
                    authenticated,
                    timeout,
                    navCtx,
                    Instant.now()
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
