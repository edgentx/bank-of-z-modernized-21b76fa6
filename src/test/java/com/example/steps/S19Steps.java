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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;
    private NavigateMenuCmd command;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        this.aggregate.markAuthenticated("teller-456"); // Ensure authenticated for valid case
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is handled in aggregate initialization
        this.command = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Updating the command instance created in previous step
        this.command = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Updating the command instance created in previous step
        this.command = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");

        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals("MAIN_MENU", navEvent.menuId());
        assertEquals("ENTER", navEvent.action());
        assertEquals("session-123", navEvent.aggregateId());
    }

    // --- Error Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // Do NOT mark authenticated
        this.command = new NavigateMenuCmd("session-auth-fail", "MAIN_MENU", "ENTER");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout");
        this.aggregate.markAuthenticated("teller-456");
        // Set activity to 20 minutes ago (timeout is 15)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        this.command = new NavigateMenuCmd("session-timeout", "MAIN_MENU", "ENTER");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-context-fail");
        this.aggregate.markAuthenticated("teller-456");
        // Provide an invalid MenuId (blank) which violates the invariant
        this.command = new NavigateMenuCmd("session-context-fail", "", "ENTER");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but command succeeded");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Expected a domain exception (IllegalStateException or IllegalArgumentException), but got: " + capturedException.getClass().getSimpleName());
    }
}