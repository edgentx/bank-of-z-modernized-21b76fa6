package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for Story S-19: TellerSession Navigation.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private String sessionId;
    private String menuId;
    private String action;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated and active
        aggregate.markAuthenticated("teller-001");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is initialized in the first Given
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "ENTER";
    }

    // --- Violation Givens ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling markAuthenticated(). Defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001"); // Auth is valid
        aggregate.expireSession(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "session-bad-state";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-001");
        // Set current menu to same value we will try to navigate to
        String stuckMenu = "DEAD_END";
        aggregate.setCurrentMenu(stuckMenu);
        this.menuId = stuckMenu; // Prepare the navigation command to match current state
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        
        DomainEvent event = events.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        
        // Verify it's a domain logic violation (IllegalStateException)
        assertTrue(capturedException instanceof IllegalStateException, 
            "Expected IllegalStateException but got: " + capturedException.getClass().getSimpleName());
        
        // Verify message matches one of our invariant rules
        String msg = capturedException.getMessage();
        assertTrue(
            msg.contains("authenticated") || 
            msg.contains("timeout") || 
            msg.contains("context"),
            "Exception message should relate to the violated invariant. Got: " + msg
        );
    }
}
