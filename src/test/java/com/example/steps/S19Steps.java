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
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private String sessionId;
    private String menuId;
    private String action;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it's in a valid state
        aggregate.markAuthenticated("TELLER-01");
        aggregate.setLastActivity(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "MAIN_MENU";
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
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(menuId, event.targetMenuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "SESSION-401";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setLastActivity(Instant.now());
        // NOT authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "SESSION-408";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        // Set activity to 1 hour ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofHours(1)));
        // Ensure threshold check triggers (default 30 mins)
        aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        sessionId = "SESSION-409";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-01");
        aggregate.setLastActivity(Instant.now());
        // Set current menu to PUBLIC
        aggregate.setCurrentMenu("PUBLIC");
        // Now trying to go to ADMIN (which violates the business rule in aggregate)
        menuId = "ADMIN";
        action = "TRY";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Exception should have been thrown");
    }
}
