package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    // Helper to create a valid basic aggregate state
    private void createValidAggregate() {
        this.aggregate = new TellerSessionAggregate("TS-01");
        // Set up valid state for the Happy Path scenario
        this.aggregate.markAuthenticated(); // Simulate successful auth (CICS/IMS)
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        createValidAggregate();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        this.aggregate = new TellerSessionAggregate("TS-NO-AUTH");
        // Intentionally NOT calling markAuthenticated(). Default is false.
        Assertions.assertFalse(aggregate.isAuthenticated(), "Teller should not be authenticated for this test");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        createValidAggregate();
        // Force the aggregate to look like it timed out already
        this.aggregate.forceTimeoutState();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        createValidAggregate();
        // Force the aggregate into a navigation state that implies activity or conflict
        this.aggregate.forceNavigationState("MENU_MAIN");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // The command will be constructed with 'TELLER-123' in the When step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // The command will be constructed with 'TERM-3270-01' in the When step
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        StartSessionCmd cmd = new StartSessionCmd(
            "TELLER-123",
            "TERM-3270-01"
        );
        try {
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultingEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultingEvents.size(), "Should produce exactly one event");
        Assertions.assertTrue(resultingEvents.get(0) instanceof SessionStartedEvent, "Event should be SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultingEvents.get(0);
        Assertions.assertEquals("TS-01", event.aggregateId());
        Assertions.assertEquals("TELLER-123", event.tellerId());
        Assertions.assertEquals("TERM-3270-01", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // We check it's a RuntimeException (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(caughtException instanceof RuntimeException, "Expected a domain exception");
    }
}
