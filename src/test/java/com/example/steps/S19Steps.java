package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private Exception thrownException;
    private String sessionId;
    private String menuId;
    private String action;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Ensure valid state
        aggregate.setCurrentMenu("MAIN_MENU"); // Ensure valid context
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId initialized in "a valid TellerSession aggregate"
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "DEPOSIT_SCREEN";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("DEPOSIT_SCREEN", event.menuId());
        assertEquals("ENTER", event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // aggregate.markAuthenticated() is NOT called
        menuId = "DEPOSIT_SCREEN";
        action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected a domain exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set last activity to 31 minutes ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
        menuId = "DEPOSIT_SCREEN";
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        sessionId = "session-bad-nav";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("DEPOSIT_SCREEN"); // Current state
        menuId = "DEPOSIT_SCREEN"; // Attempt to navigate to same state
        action = "ENTER";
    }
}