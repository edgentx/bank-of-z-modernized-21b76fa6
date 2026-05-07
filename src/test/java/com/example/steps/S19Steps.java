package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
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
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.sessionId = "sess-123";
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
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(this.capturedException, "Should not have thrown an exception");
        assertNotNull(this.resultEvents);
        assertEquals(1, this.resultEvents.size());
        assertTrue(this.resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) this.resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(this.menuId, event.menuId());
        assertEquals(this.action, event.action());
    }

    // --- Scenarios for Invariants ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "sess-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(false); // Violation
        this.aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago (timeout is 15)
        this.aggregate.setLastActivityAt(Instant.now().minusSeconds(1200)); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavContext() {
        this.sessionId = "sess-locked";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivityAt(Instant.now());
        // Set state to LOCKED
        this.aggregate.setCurrentMenuId("LOCKED");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(this.capturedException);
        assertTrue(this.capturedException instanceof IllegalStateException);
        // Optional: Check specific message content if required, though class type is usually sufficient for BDD
    }
}
