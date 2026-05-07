package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Context for test setup
    private String testTellerId = "TELLER-001";
    private String testTerminalId = "TERM-A01";
    private boolean testAuth = true;
    private String testContext = "MAIN_MENU";

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.testTellerId = "TELLER-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.testTerminalId = "TERM-A01";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
            "SESSION-123",
            this.testTellerId,
            this.testTerminalId,
            this.testAuth,
            this.testContext
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("TELLER-001", event.tellerId());
        Assertions.assertEquals("TERM-A01", event.terminalId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("SESSION-FAIL-AUTH");
        this.testAuth = false; // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-FAIL-TIMEOUT");
        // Simulate an old aggregate created 31 minutes ago
        Instant past = Instant.now().minusSeconds(31 * 60);
        aggregate.setLastActivityAt(past);
        // Note: The validation logic checks the time difference against now.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("SESSION-FAIL-NAV");
        this.testContext = ""; // Violation: blank context
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it's the domain logic error (IllegalStateException usually used for invariants)
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
