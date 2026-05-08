package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String providedSessionId;
    private String providedMenuId;
    private String providedAction;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        providedSessionId = "ts-session-123";
        aggregate = new TellerSessionAggregate(providedSessionId);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in setup, but we assert it exists
        assertNotNull(providedSessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        providedMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        providedAction = "SELECT";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        providedSessionId = "ts-session-unauth";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.setAuthenticated(false);
        providedMenuId = "MENU_X";
        providedAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        providedSessionId = "ts-session-timeout";
        aggregate = new TellerSessionAggregate(providedSessionId);
        // Set last activity to 20 minutes ago to simulate timeout
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        providedMenuId = "MENU_Y";
        providedAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        providedSessionId = "ts-session-bad-ctx";
        aggregate = new TellerSessionAggregate(providedSessionId);
        aggregate.setOperational(false); // Invalid context
        providedMenuId = "MENU_Z";
        providedAction = "ENTER";
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(providedSessionId, providedMenuId, providedAction);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(providedMenuId, event.menuId());
        assertEquals(providedAction, event.action());
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We use IllegalStateException for domain invariant violations in the aggregate
        assertTrue(caughtException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
