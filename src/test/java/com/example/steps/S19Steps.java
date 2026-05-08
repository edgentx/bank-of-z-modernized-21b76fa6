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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // By default, we make the aggregate authenticated and active for the success case
        aggregate.markAuthenticated("TELLER_01");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already set in the previous step
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    // --- Rejection Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "SESSION-UNAUTH";
        // Create aggregate but DO NOT call markAuthenticated. It defaults to !authenticated.
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.menuId = "MAIN_MENU";
        this.action = "SELECT";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "SESSION-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Authenticate to pass the first check
        aggregate.markAuthenticated("TELLER_01");
        // Set last activity to 20 minutes ago (violating the 15 min timeout defined in Aggregate)
        Instant past = Instant.now().minus(20, ChronoUnit.MINUTES);
        aggregate.setLastActivityAt(past);
        
        this.menuId = "MAIN_MENU";
        this.action = "SELECT";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "SESSION-INVALID-STATE";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Authenticate as a regular teller (not SUPERVISOR_01)
        aggregate.markAuthenticated("TELLER_REGULAR");
        
        // Attempt to navigate to an Admin menu which requires SUPERVISOR_01
        this.menuId = "MENU_ADMIN_DASHBOARD";
        this.action = "OPEN";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "An exception should have been thrown");
        // We expect IllegalStateException for Domain Invariants
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException, got " + capturedException.getClass().getSimpleName());
        
        // Ensure no events were committed
        assertNull(resultEvents, "No events should be produced on rejected command");
    }
}
