package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // We assume for success cases it is authenticated and active
        aggregate.markAuthenticated("teller-1");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-123");
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-1");
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-1");
        aggregate.markInvalidNavigationState();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // The command constructor handles validation, so we just ensure we use a good ID.
        // The specific ID value doesn't matter for the logic of the aggregate itself beyond existence.
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the 'When' step construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the 'When' step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // We use dummy valid data for the positive test cases.
            // In a negative test, the aggregate state pre-checks failure before validation of command data.
            this.command = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            this.resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be instance of MenuNavigatedEvent");

        MenuNavigatedEvent navigatedEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navigatedEvent.type());
        assertEquals("session-123", navigatedEvent.aggregateId());
        assertNotNull(navigatedEvent.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Domain rules typically throw IllegalStateException or IllegalArgumentException.
        // We verify that execution failed as expected.
        assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException), got: " + capturedException.getClass()
        );
        assertNull(resultEvents, "No events should be produced when command is rejected");
    }
}