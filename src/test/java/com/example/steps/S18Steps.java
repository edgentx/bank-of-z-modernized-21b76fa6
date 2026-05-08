package com.example.steps;

import com.example.domain.DomainError;
import com.example.domain.SessionStartedEvent;
import com.example.domain.StartSessionCmd;
import com.example.domain.TellerSession;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class S18Steps {

    private TellerSession aggregate;
    private StartSessionCmd command;
    private Exception caughtException;
    private Object resultingEvent;

    // Helper to create a clean slate for success scenarios
    private TellerSession createValidSession() {
        return TellerSession.create();
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = createValidSession();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        if (command == null) command = new StartSessionCmd();
        command.setTellerId("TELLER_001");
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        if (command == null) command = new StartSessionCmd();
        command.setTerminalId("TERM_3270_01");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Scenario: Trying to start without valid auth context (simulated)
        aggregate = TellerSession.create();
        command = new StartSessionCmd();
        command.setTellerId(null); // Violation: Null teller ID implies no authenticated user context
        command.setTerminalId("TERM_3270_01");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout_config() {
        // Scenario: Configuration violation - e.g., negative timeout
        aggregate = TellerSession.create();
        command = new StartSessionCmd();
        command.setTellerId("TELLER_001");
        command.setTerminalId("TERM_3270_01");
        command.setTimeoutConfig(Duration.ofSeconds(-10)); // Invalid configuration
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        // Scenario: Starting a session with an invalid or malformed navigation state request
        aggregate = TellerSession.create();
        command = new StartSessionCmd();
        command.setTellerId("TELLER_001");
        command.setTerminalId("TERM_3270_01");
        command.setInitialContext("INVALID_CONTEXT"); // Violation
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            resultingEvent = aggregate.execute(command);
        } catch (DomainError e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultingEvent, "Expected an event to be emitted");
        Assertions.assertTrue(resultingEvent instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        SessionStartedEvent event = (SessionStartedEvent) resultingEvent;
        Assertions.assertEquals("TELLER_001", event.getTellerId());
        Assertions.assertEquals("TERM_3270_01", event.getTerminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a DomainError to be thrown");
        Assertions.assertTrue(caughtException instanceof DomainError);
    }
}
