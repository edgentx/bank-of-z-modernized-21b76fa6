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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    // --- Scenarios Setup ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Mark authenticated to satisfy basic validity
        aggregate.markAuthenticated("teller-001");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate creation, but can verify cmd construction later
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Setup state
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Setup state
    }

    // --- Action ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Default valid command values if not overridden by specific scenario setup
            String targetMenu = (cmd != null) ? cmd.menuId() : "MAIN_MENU";
            String action = (cmd != null) ? cmd.action() : "ENTER";
            
            // Construct command (allows specific scenarios to potentially set specifics before this hook runs)
            NavigateMenuCmd executeCmd = new NavigateMenuCmd("session-123", targetMenu, action);
            
            resultingEvents = aggregate.execute(executeCmd);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultingEvents = null;
        }
    }

    // --- Success Outcomes ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertEquals(1, resultingEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals("session-123", navEvent.aggregateId());
    }

    // --- Failure Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do NOT mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-001");
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(20 * 60));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.markAuthenticated("teller-001");
        // Set current context to MAIN
        aggregate.setCurrentMenuId("MAIN");
        // Attempt to go to a restricted state defined in Aggregate logic (ADMIN_DASHBOARD)
        cmd = new NavigateMenuCmd("session-bad-nav", "ADMIN_DASHBOARD", "TRY");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Check for specific exception types or messages based on domain rules
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
