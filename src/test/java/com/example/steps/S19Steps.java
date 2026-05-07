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
    private Exception capturedException;

    // Helper to reset state
    private void initAggregate(String sessionId) {
        aggregate = new TellerSessionAggregate(sessionId);
        // Default to authenticated for generic scenario unless specified otherwise by 'Given'
        aggregate.markAuthenticated("teller-123");
        resultEvents = null;
        capturedException = null;
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        initAggregate("session-1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create aggregate but deliberately skip authentication
        aggregate = new TellerSessionAggregate("session-unauth");
        // Ensure last activity is recent so we don't hit the timeout error first
        aggregate.setLastActivity(java.time.Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        initAggregate("session-timeout");
        // Force the aggregate into a timed-out state
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        initAggregate("session-bad-context");
        aggregate.markContextInvalid();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by aggregate initialization in Given steps
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Data for the command is provided in the When step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Data for the command is provided in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Construct command with valid data
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but command succeeded.");
        assertTrue(capturedException instanceof IllegalStateException);
    }
}
