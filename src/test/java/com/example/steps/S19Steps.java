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
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid base state
        aggregate.markAuthenticated("TELLER-01");
        aggregate.markActive();
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setCurrentMenuId("MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set in previous step
        Assertions.assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "DEPOSIT_SCREEN";
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
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals("DEPOSIT_SCREEN", event.targetMenuId());
    }

    // Scenarios for Rejections

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "SESSION-401";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally not calling markAuthenticated()
        aggregate.markActive(); // Active but not auth'd
        this.menuId = "ADMIN_SCREEN";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesSessionTimeout() {
        sessionId = "SESSION-408";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-02");
        aggregate.markActive();
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(java.time.Duration.ofMinutes(20)));
        this.menuId = "PROFILE";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        sessionId = "SESSION-409";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-03");
        // Intentionally inactive
        // aggregate.markActive(); <-- SKIPPED
        this.menuId = "LOCKED_MENU";
        this.action = "BYPASS";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // We expect IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected IllegalStateException or IllegalArgumentException, but got: " + capturedException.getClass().getSimpleName()
        );
    }
}
