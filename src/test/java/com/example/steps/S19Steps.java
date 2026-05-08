package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    private static final String SESSION_ID = "session-123";
    private static final String MENU_ID = "MainMenu";
    private static final String ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(); // Helper to set internal state for valid scenario
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // implicitly handled by constructor in 'aValidTellerSessionAggregate'
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // implicitly handled by command construction in 'whenTheNavigateMenuCmdCommandIsExecuted'
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // implicitly handled by command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, MENU_ID, ACTION);
            // Reload aggregate from repo to ensure persistence logic if needed, or just use instance
            // For simplicity in unit test steps, we use the instance we have.
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals(SESSION_ID, event.aggregateId());
        Assertions.assertEquals(MENU_ID, event.menuId());
        Assertions.assertEquals(ACTION, event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Not calling markAuthenticated() leaves it in default state (unauthenticated)
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        aggregate.markExpired(); // Helper to set lastActivity to ancient history
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated();
        repo.save(aggregate);
    }

    // Specific When for the invalid context scenario (passing invalid data)
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        try {
            // Passing null/blank to violate the "accurately reflect" invariant
            NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, "", "");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Exception should have been thrown");
        // Domain errors in this pattern are RuntimeExceptions (IllegalStateException/IllegalArgumentException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
