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
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup a valid, authenticated, active session state
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set in previous step
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(false); // Not authenticated
        aggregate.setActive(true);
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        // Set activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        sessionId = "SESSION-INACTIVE";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setAuthenticated(true); // Authenticated but not active context
        aggregate.setActive(false);
        aggregate.setLastActivityAt(Instant.now());
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We expect IllegalStateException for domain invariant violations
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}