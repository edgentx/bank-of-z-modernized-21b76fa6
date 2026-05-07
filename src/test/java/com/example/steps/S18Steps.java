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

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private Exception capturedException;
    private DomainEvent resultingEvent;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state by default
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Do NOT mark authenticated - defaults to false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Set last activity to a long time ago to simulate timeout
        aggregate.setLastActivityAt(java.time.Instant.now().minusSeconds(7200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.markAuthenticated();
        aggregate.setNavigationState("INVALID_CTX");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        this.terminalId = "terminal-A";
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        Command cmd = new StartSessionCmd(aggregate.id(), tellerId, terminalId);
        try {
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultingEvent = events.get(0);
            }
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultingEvent, "Event should not be null");
        assertTrue(resultingEvent instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        SessionStartedEvent event = (SessionStartedEvent) resultingEvent;
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
