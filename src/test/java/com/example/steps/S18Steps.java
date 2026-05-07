package com.example.steps;

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
    private StartSessionCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario: Successfully execute StartSessionCmd
    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state (authenticated, not timed out, correct nav state)
        aggregate.markAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Handled in the command construction below, just a placeholder step
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Handled in the command construction below
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Create command with valid IDs
            command = new StartSessionCmd("session-123", "teller-42", "term-01");
            resultEvents = aggregate.execute(command);
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
        assertEquals("session-123", event.aggregateId());
        assertEquals("teller-42", event.tellerId());
        assertEquals("term-01", event.terminalId());
        
        // Verify aggregate state updated
        assertEquals(TellerSessionAggregate.SessionStatus.ACTIVE, aggregate.getStatus());
    }

    // Scenario: Rejected - Auth
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.markAuthenticated(false); // Violation
        aggregate.setNavigationState(TellerSessionAggregate.NavigationState.IDLE);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
        assertTrue(capturedException.getMessage().contains("authenticated"));
    }

    // Scenario: Rejected - Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated(true);
        aggregate.setNavigationState(TellerSessionAggregate.NavigationState.IDLE);
        // Set time far in the past to simulate timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofHours(1)));
    }

    // Scenario: Rejected - Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated(true);
        // Set state to something other than IDLE (expected for start)
        aggregate.setNavigationState(TellerSessionAggregate.NavigationState.BATCH_PROCESSING); 
        aggregate.setLastActivityAt(Instant.now());
    }
}
