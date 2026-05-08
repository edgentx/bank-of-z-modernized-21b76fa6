package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private String resultEventId;
    private String currentMenuId;
    private String currentAction;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01");
        aggregate.setSessionActive(true);
        aggregate.setLastActivity(java.time.Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateNotAuthenticated() {
        aggregate = new TellerSessionAggregate("session-123");
        // Explicitly NOT authenticated
        aggregate.setSessionActive(true);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatIsTimedOut() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01");
        // Set activity to 20 minutes ago (timeout is 15)
        aggregate.setLastActivity(java.time.Instant.now().minus(java.time.Duration.ofMinutes(20)));
        aggregate.setSessionActive(true);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateInvalidContext() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01");
        // Context is invalid because session is not active
        aggregate.setSessionActive(false);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly in scenario setup via 'a valid TellerSession aggregate'
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.currentMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.currentAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            var cmd = new NavigateMenuCmd(aggregate.id(), currentMenuId, currentAction);
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                resultEventId = events.get(0).type();
            }
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(aggregate.uncommittedEvents());
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertEquals("menu.navigated", aggregate.uncommittedEvents().get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}