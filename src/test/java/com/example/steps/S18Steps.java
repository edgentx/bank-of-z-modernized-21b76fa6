package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.uimodel.model.SessionStartedEvent;
import com.example.domain.uimodel.model.StartSessionCmd;
import com.example.domain.uimodel.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String currentTellerId;
    private String currentTerminalId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Ensure baseline valid state
        aggregate.resetToValidState();
        aggregate.markAuthenticated(); // Requirement for success
    }

    @Given("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        this.currentTellerId = "teller-01";
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        this.currentTerminalId = "term-A";
    }

    // --- Violation States ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        aggregate.resetToValidState();
        // isAuthenticated defaults to false, which violates the invariant
        // Ensure other invariants are valid so we isolate this failure
        aggregate.markAuthenticated(); // 
        // Actually, the prompt says: "violates: A teller must be authenticated"
        // So we explicitly UNSET it or leave it false. 
        aggregate.resetToValidState(); // Resets auth to false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.resetToValidState();
        aggregate.markAuthenticated(); // Pass auth
        aggregate.markTimedOut(); // Violate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("session-nav-error");
        aggregate.resetToValidState();
        aggregate.markAuthenticated(); // Pass auth
        aggregate.markNavigationInvalid(); // Violate nav state
    }

    // --- Actions ---

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(), 
                this.currentTellerId != null ? this.currentTellerId : "teller-default",
                this.currentTerminalId != null ? this.currentTerminalId : "term-default"
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Outcomes ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent, "Expected SessionStartedEvent");
        
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        assertEquals("session.started", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException for domain rule violation");
    }

}
