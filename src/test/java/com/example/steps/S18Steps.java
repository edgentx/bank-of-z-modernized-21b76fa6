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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String tellerId;
    private String terminalId;
    private String navigationState;
    private boolean isAuthenticated;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.tellerId = "teller-001";
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.terminalId = "term-42";
    }

    // Default valid state for positive scenarios
    private void setupDefaults() {
        if (tellerId == null) this.tellerId = "teller-001";
        if (terminalId == null) this.terminalId = "term-42";
        this.isAuthenticated = true;
        this.navigationState = "HOME";
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        setupDefaults();
        Command cmd = new StartSessionCmd(tellerId, terminalId, isAuthenticated, navigationState);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNull(thrownException, "Should not throw exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        a_valid_TellerSession_aggregate();
        a_valid_tellerId_is_provided();
        a_valid_terminalId_is_provided();
        this.isAuthenticated = false; // Violation
        this.navigationState = "HOME";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        a_valid_TellerSession_aggregate();
        a_valid_tellerId_is_provided();
        a_valid_terminalId_is_provided();
        this.isAuthenticated = true;
        this.navigationState = "HOME";
        aggregate.markInactive(); // Simulate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation() {
        a_valid_TellerSession_aggregate();
        a_valid_tellerId_is_provided();
        a_valid_terminalId_is_provided();
        this.isAuthenticated = true;
        this.navigationState = "INVALID_STATE"; // Violation
    }
}
