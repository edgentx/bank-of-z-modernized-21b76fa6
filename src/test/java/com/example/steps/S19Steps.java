package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup valid authenticated state
        aggregate.markAuthenticated("teller-001");
        aggregate.setLastActivityTime(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-401");
        // Ensure unauthenticated
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-001");
        // Set last activity to 20 minutes ago (Configured timeout is 15m)
        aggregate.setLastActivityTime(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-bad-nav");
        aggregate.markAuthenticated("teller-001");
        aggregate.setLastActivityTime(Instant.now());
        // Set current state to ROOT
        aggregate.setCurrentMenu("ROOT");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly in aggregate construction, but used to construct command
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in When block via command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in When block via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        String sessionId = aggregate.id();
        String menuId = "ACCOUNT_SUMMARY";
        String action = "ENTER";
        
        // Specific violation for the bad navigation state scenario
        if ("ROOT".equals(aggregate.getCurrentMenuId())) {
            // Trying to go to DETAILS from ROOT triggers the context violation logic in the aggregate
            menuId = "TRANSACTION_DETAILS"; 
            action = "SELECT"; 
        }

        this.command = new NavigateMenuCmd(sessionId, menuId, action);
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(command.menuId(), event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Verify it's an IllegalStateException (domain error)
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        System.out.println("Expected error caught: " + thrownException.getMessage());
    }
}
