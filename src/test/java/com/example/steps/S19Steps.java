package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String testSessionId = "session-123";
    private String testMenuId = "MAIN_MENU";
    private String testAction = "ENTER";

    // --- Scenario: Successfully execute NavigateMenuCmd ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated("teller-01"); // Ensure authenticated
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by initialization
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled by initialization
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled by initialization
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(testSessionId, testMenuId, testAction);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(testSessionId, event.aggregateId());
        assertEquals(testMenuId, event.currentMenuId());
        assertEquals(testAction, event.action());
    }

    // --- Scenario: NavigateMenuCmd rejected (Authentication) ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(testSessionId);
        // Intentionally NOT calling markAuthenticated
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("authenticated"));
    }

    // --- Scenario: NavigateMenuCmd rejected (Timeout) ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated("teller-01");
        aggregate.expireSession(); // Force timeout
    }

    // --- Scenario: NavigateMenuCmd rejected (Navigation Context) ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(testSessionId);
        aggregate.markAuthenticated("teller-01");
        // We simulate a violation by attempting to navigate with invalid inputs in the step definition
        // or by modifying the aggregate state such that it rejects the command.
        // For this test, we will pass a blank menuId in the execution step.
        testMenuId = ""; // Invalid state
    }
}