package com.example.steps;

import com.example.domain.menu.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate session;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-123";
        this.session = new TellerSessionAggregate(sessionId);
        // Seed state to be valid
        session.seedState(TellerSessionAggregate.Status.ACTIVE, "teller-1", Instant.now().minusSeconds(60), "MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the aggregate setup
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "ACCOUNTS_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = session.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
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
        this.sessionId = "sess-unauth";
        this.session = new TellerSessionAggregate(sessionId);
        // Seed state as UNAUTHENTICATED (e.g. null tellerId)
        session.seedState(TellerSessionAggregate.Status.UNAUTHENTICATED, null, Instant.now().minusSeconds(60), "LOGIN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.session = new TellerSessionAggregate(sessionId);
        // Seed state with old lastActivityAt
        Instant oldTime = Instant.now().minus(Duration.ofMinutes(30)); // Assuming timeout < 30 mins
        session.seedState(TellerSessionAggregate.Status.ACTIVE, "teller-1", oldTime, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavContext() {
        this.sessionId = "sess-badctx";
        this.session = new TellerSessionAggregate(sessionId);
        // Seed state where action is invalid for the current screen
        // e.g. trying to perform 'DEPOSIT' on 'LOGIN' screen
        session.seedState(TellerSessionAggregate.Status.ACTIVE, "teller-1", Instant.now().minusSeconds(10), "LOGIN_SCREEN");
        this.menuId = "DEPOSIT_INPUT"; // Cannot jump here from Login
        this.action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        // We expect IllegalStateException or IllegalArgumentException based on implementation
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
