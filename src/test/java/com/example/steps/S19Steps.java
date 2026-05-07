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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it's authenticated for standard valid scenarios
        aggregate.markAuthenticated();
        aggregate.markSessionActive();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicit in the aggregate creation above, but we ensure it matches
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Placeholder for navigation command context
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Placeholder for navigation command context
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "VIEW");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do not mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markSessionStale(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        String sessionId = "session-bad-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.markContextIdle(); // Ensure we are NOT in transaction
    }

    @When("the NavigateMenuCmd command is executed on invalid state")
    public void theNavigateMenuCmdCommandIsExecutedOnInvalidState() {
        try {
            // Using a specific menu that requires active transaction context based on the aggregate logic
            NavigateMenuCmd cmd;
            if (aggregate.isTimedOut()) {
                 cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "VIEW");
            } else {
                 // Try to access transaction details while idle
                 cmd = new NavigateMenuCmd(aggregate.id(), "TXN_DETAILS", "VIEW");
            }
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | UnknownCommandException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
        // Verify the message matches the invariants
        assertTrue(thrownException.getMessage().contains("must be authenticated") ||
                   thrownException.getMessage().contains("timeout") ||
                   thrownException.getMessage().contains("current operational context"));
    }
}
