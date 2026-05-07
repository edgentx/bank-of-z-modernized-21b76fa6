package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private NavigateMenuCmd currentCmd;

    // --- Scenario 1: Success ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid base state
        aggregate.setLastActivityTo(Instant.now()); // Ensure active
        aggregate.setOperationalContext(null); // Clean context
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate initialization, normally we verify ID matches
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in When
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in When
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Default to valid data for success case
            String menuId = "MAIN_MENU";
            String action = "SELECT";
            String tellerId = "teller-1";
            
            // If not already set by a specific 'Given' violating context
            if (currentCmd == null) {
                currentCmd = new NavigateMenuCmd(aggregate.id(), menuId, action, tellerId, aggregate.getCurrentMenuId());
            }
            
            resultEvents = aggregate.execute(currentCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
    }

    // --- Scenario 2: Auth Invariant ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do NOT mark authenticated
        aggregate.setLastActivityTo(Instant.now());
    }

    // --- Scenario 3: Timeout Invariant ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Set activity to past > 15 mins
        aggregate.setLastActivityTo(Instant.now().minus(Duration.ofMinutes(20)));
    }

    // --- Scenario 4: Context Invariant ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-context");
        aggregate.markAuthenticated();
        aggregate.setOperationalContext("CTX-OLD");
        
        // Prepare a command that claims a different context (or null) than what the aggregate holds
        currentCmd = new NavigateMenuCmd(aggregate.id(), "NEW_MENU", "SELECT", "teller-1", "WRONG_CTX");
    }

    // --- Common Rejection Steps ---

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception but command succeeded");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
