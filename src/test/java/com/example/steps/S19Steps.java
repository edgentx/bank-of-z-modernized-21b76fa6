package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions for S-19 (TellerSession Navigation).
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // To be valid, the session must be authenticated (inferred from other scenarios)
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        aggregate = new TellerSessionAggregate("session-unauth-123");
        // Explicitly ensure the session is NOT authenticated.
        // TellerSessionAggregate defaults to unauthenticated, so we just don't call markAuthenticated().
        aggregate.setAuthenticated(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-123");
        aggregate.markAuthenticated(); // Valid auth, but...
        aggregate.markTimedOut();       // ...force the state to be timed out.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-context-123");
        aggregate.markAuthenticated();
        // We will drive the violation via the inputs provided in the 'When' step (e.g. nulls),
        // or we could have set a specific state that rejects the inputs.
        // For S-19, the command validates the context.
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by aggregate initialization
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in When step construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in When step construction
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Determine inputs based on context. If we are in the "Context" violation scenario,
            // we send invalid inputs to trigger the validation error.
            if (aggregate.id().equals("session-context-123")) {
                command = new NavigateMenuCmd("session-context-123", "", ""); // Blank inputs to violate context
            } else {
                command = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            }

            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected list of events, but got null");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("MAIN_MENU", event.menuId());
        assertEquals("ENTER", event.action());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown, but it wasn't");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Expected domain error (IllegalStateException or IllegalArgumentException), got: " + capturedException.getClass().getSimpleName());
    }

}
