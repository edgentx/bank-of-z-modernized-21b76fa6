package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.Assert.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Scenario 1: Success ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Pre-condition: Authenticated and active
        aggregate.markAuthenticated("teller-01");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate creation in 'aValidTellerSessionAggregate'
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Used in command execution
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Used in command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull("No events were emitted", resultEvents);
        assertFalse("Expected at least one event", resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertNull("Expected no exception, but got: " + capturedException, capturedException);
    }

    // --- Scenario 2: Unauthenticated ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do NOT authenticate
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull("Expected an exception to be thrown", capturedException);
        assertTrue("Expected IllegalStateException", capturedException instanceof IllegalStateException);
    }

    // --- Scenario 3: Timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-01");
        // Force timeout state
        aggregate.markTimedOut();
        repository.save(aggregate);
    }

    // --- Scenario 4: Invalid Context ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated("teller-01");
        repository.save(aggregate);
    }
    
    // Overriding When for negative context test to send bad data
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        try {
            // Simulate invalid context by passing invalid IDs
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-bad-context", null, null);
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            capturedException = e;
        }
    }

}
