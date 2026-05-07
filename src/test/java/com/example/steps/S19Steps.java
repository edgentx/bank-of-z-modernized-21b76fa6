package com.example.steps;

import com.example.domain.shared.DomainEvent;
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
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.setAuthenticated(true);
        aggregate.setCurrentScreenId("MAIN_MENU");
        aggregate.setLastActivityAt(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate initialization, placeholder for semantic clarity
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            command = new NavigateMenuCmd("session-123", "ACCOUNT_DETAILS", "ENTER");
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("ACCOUNT_DETAILS", event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        aggregate.setAuthenticated(false); // Violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago (default timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-locked");
        aggregate.setAuthenticated(true);
        aggregate.setCurrentScreenId("LOCKED"); // Violation context
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        // Verify message contains invariant details for debugging
        assertTrue(thrownException.getMessage().length() > 0);
    }
}