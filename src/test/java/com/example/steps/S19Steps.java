package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String inputSessionId;
    private String inputMenuId;
    private String inputAction;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state
        aggregate.setState("MAIN_MENU", true, Instant.now(), Duration.ofMinutes(15));
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.inputSessionId = "session-123";
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.inputMenuId = "ACCOUNT_INQUIRY";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        this.inputAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(inputSessionId, inputMenuId, inputAction);
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
        assertEquals(inputMenuId, event.menuId());
        assertEquals(inputAction, event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-123");
        // Not authenticated
        aggregate.setState("MAIN_MENU", false, Instant.now(), Duration.ofMinutes(15));
        this.inputSessionId = "session-123";
        this.inputMenuId = "ACCOUNT_INQUIRY";
        this.inputAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-123");
        // Authenticated, but last activity was 20 mins ago (timeout is 15)
        Instant oldTime = Instant.now().minus(20, ChronoUnit.MINUTES);
        aggregate.setState("MAIN_MENU", true, oldTime, Duration.ofMinutes(15));
        this.inputSessionId = "session-123";
        this.inputMenuId = "ACCOUNT_INQUIRY";
        this.inputAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-123");
        // Authenticated, Active, but no context (currentMenuId is null) yet trying to execute action
        aggregate.setState(null, true, Instant.now(), Duration.ofMinutes(15));
        this.inputSessionId = "session-123";
        this.inputMenuId = "DETAILS"; 
        this.inputAction = "ACTION"; // Trigger specific logic check in aggregate
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}