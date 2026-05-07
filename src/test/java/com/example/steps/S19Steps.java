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

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01"); // Ensure valid state
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in constructor/command creation
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command creation
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER", "teller-01", null);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
    }

    // Scenario 2: Authentication Failure
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally NOT calling markAuthenticated
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertTrue(thrownException.getMessage().contains("Authentication required"));
    }

    // Scenario 3: Timeout Failure
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-01");
        aggregate.expireSession();
    }

    // Scenario 4: Context Failure
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-locked");
        aggregate.markAuthenticated("teller-01");
        aggregate.lockContext("ADMIN_ONLY");
        // We'll pass a currentScreen in the cmd that triggers the business rule check
    }

    @Override
    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecutedForErrorCases() {
        // For the context violation scenario, we pass a specific screen indicator
        String screenIndicator = aggregate.getClass().getSimpleName().contains("Locked") ? "BLOCKED_STATE" : null;
        
        // Note: The test logic here adapts to the context. 
        // In the lock scenario, the aggregate is locked.
        // We need to construct a command that triggers the specific check.
        // Let's simplify: The lock check in aggregate happens internally.
        // We just need to call execute.
        
        cmd = new NavigateMenuCmd(aggregate.id(), "TARGET_MENU", "ENTER", "teller-01", "BLOCKED_STATE");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}