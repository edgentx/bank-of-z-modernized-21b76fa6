package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set up valid state
        this.aggregate.markAuthenticated("teller-1");
        repo.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.sessionId = "session-123";
        // Reload to ensure cleanliness
        this.aggregate = repo.load(sessionId);
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "view";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(menuId, event.menuId());
        Assertions.assertEquals(action, event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT mark authenticated
        repo.save(aggregate);
        // Set up valid inputs for command to ensure failure is due to auth
        this.menuId = "MAIN_MENU";
        this.action = "view";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // In this implementation, we throw IllegalStateException for domain rule violations
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated("teller-1");
        this.aggregate.markTimedOut();
        repo.save(aggregate);
        
        this.menuId = "MAIN_MENU";
        this.action = "view";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "session-bad-state";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated("teller-1");
        // Force a state that is invalid for transition (logic inside aggregate)
        this.aggregate.forceContextViolation();
        repo.save(aggregate);
        
        this.menuId = "DISALLOWED_MENU";
        this.action = "forced";
    }
}