package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSession aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSession("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setCurrentMenuId("MAIN_MENU");
        aggregate.setCurrentContext("DEFAULT");
        aggregate.setLastActivityAt(Instant.now());
        aggregate.setActive(true);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSession("session-unauth");
        aggregate.setAuthenticated(false); // Violates invariant
        aggregate.setCurrentMenuId("MAIN_MENU");
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSession("session-timeout");
        aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago to violate the 15 minute timeout
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSession("session-nav-state");
        aggregate.setAuthenticated(true);
        aggregate.setLastActivityAt(Instant.now());
        // The aggregate thinks it is at MENU_A, but we will send a command saying current is MENU_B
        aggregate.setCurrentMenuId("MENU_A");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly in setup
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the When step setup
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the When step setup
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            String currentMenu = (aggregate.getCurrentMenuId() != null) ? aggregate.getCurrentMenuId() : "MAIN_MENU";
            // In the violation case for NavState, we intentionally mismatch
            if ("session-nav-state".equals(aggregate.id())) {
                currentMenu = "MENU_B";
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(
                aggregate.id(),
                currentMenu,
                "DEFAULT",
                "TARGET_MENU",
                "TARGET_CONTEXT",
                "GOTO"
            );
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
        assertEquals("TARGET_MENU", event.newMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Check for specific exception types or messages based on the scenario
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
