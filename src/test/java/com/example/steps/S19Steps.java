package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
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
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Set up a valid default state: authenticated, active, on a specific menu
        this.aggregate.markAuthenticated();
        this.aggregate.setCurrentMenuId("MAIN_MENU");
        this.aggregate.setLastActivityAt(Instant.now());
        this.thrownException = null;
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate construction in the previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the 'When' step via command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the 'When' step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "ACCOUNTS_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals("ACCOUNTS_MENU", event.menuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.setCurrentMenuId("MAIN_MENU");
        this.aggregate.setAuthenticated(false); // Explicitly unauthenticated
        this.aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated();
        this.aggregate.setCurrentMenuId("MAIN_MENU");
        // Set activity to 31 minutes ago (Timeout is 30 mins in aggregate)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated();
        this.aggregate.setCurrentMenuId("ACCOUNTS_MENU"); // Currently on this menu
        this.aggregate.setLastActivityAt(Instant.now());
        
        // We will try to navigate to "ACCOUNTS_MENU" in the When step (reusing the generic method)
    }

    // Reuse the generic When for the negative scenarios, but we need to customize the command parameters for the last scenario.
    // Since Cucumber 'When' is generic, we can modify the steps to pass data or handle logic here.
    // For simplicity in this pattern, we check the exception type/message.
    
    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Exception should have been thrown");
        // In Java domain modeling, exceptions like IllegalStateException act as domain errors
        Assertions.assertTrue(thrownException instanceof IllegalStateException, "Exception should be IllegalStateException");
    }

    // Override for the specific navigation state scenario to hit the "already on this menu" logic
    @When("the NavigateMenuCmd command is executed on the same menu")
    public void theNavigateMenuCmdCommandIsExecutedOnSameMenu() {
         try {
            // Attempt to go to the menu we are already on (ACCOUNTS_MENU)
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "ACCOUNTS_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}
