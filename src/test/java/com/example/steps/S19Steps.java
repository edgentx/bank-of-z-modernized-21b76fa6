package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "TS-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.clearEvents();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "ACCOUNT_INQUIRY";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
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
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "TS-401";
        aggregate = new TellerSessionAggregate(sessionId);
        // aggregate.markAuthenticated() is NOT called, leaving isAuthenticated = false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "TS-408";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Valid auth
        aggregate.markExpired(); // Set time in past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "TS-400";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // We pass an invalid menuId in the 'aValidMenuIdIsProvided' step effectively, 
        // or we could corrupt internal state to enforce a check. 
        // The logic for this specific invariant check in the aggregate validates the input.
        // So we will provide a bad menuId.
    }

    // Override specific steps for negative flows if needed, or reuse generic ones
    @And("a valid menuId is provided")
    public void aBadMenuIdIsProvidedForContextViolation() {
        if ("TS-400".equals(sessionId)) {
            menuId = "INVALID@@@";
        } else {
            menuId = "MAIN_MENU";
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected exception but command succeeded");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}