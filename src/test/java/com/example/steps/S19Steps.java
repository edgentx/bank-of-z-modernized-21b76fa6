package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure the aggregate is in a valid state (authenticated, active)
        aggregate.markAuthenticated("TELLER-01");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set up in previous step
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
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
        assertEquals(sessionId, event.aggregateId());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "TS-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally do not call markAuthenticated()
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesInactivity() {
        sessionId = "TS-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        aggregate.markTimedOut(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "TS-BAD-STATE";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        // Simulate invalid context by providing a null/blank menu ID later, or forcing a specific state
        // Here we set up the aggregate such that any navigation is invalid (e.g. locked)
        // However, per the invariant description, we will test via the Command's input validity or aggregate state.
        // Let's use an invalid menu ID input in the scenario flow to trigger the exception.
    }

    // Specific context setter for the Bad State scenario (overwrite valid inputs)
    @And("a valid menuId is provided") // Reusing for valid flow, for bad flow we might need a different 'And' or just rely on the setup
    public void setupInvalidMenuInput() {
         // Hook to invalidate the input for the specific scenario where needed
         if (sessionId.equals("TS-BAD-STATE")) {
             this.menuId = ""; // Invalid input triggers context invariant error
         } else {
             this.menuId = "MAIN_MENU";
         }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Verify it's the specific type of error expected (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Setup for specific scenario overrides
    @Given("a valid menuId is provided")
    public void setupValidMenuId() {
        // Default valid menu
        if (this.menuId == null) this.menuId = "MAIN_MENU";
    }
}