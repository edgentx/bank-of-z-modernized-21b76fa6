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

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Given Steps ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Setup pre-condition: User is authenticated
        aggregate.markAuthenticated("teller-01");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.setUnauthenticated(); // Ensure not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-02");
        // Force timeout
        aggregate.markSessionTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        String sessionId = "session-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-03");
        // This setup doesn't inherently break state, the invalid command sent in 'When' will test the guard.
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicitly handled by the aggregate ID initialization, but used to construct cmd
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in 'When'
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in 'When'
    }

    // --- When Steps ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        String sid = aggregate.id();
        String tid = "teller-01";
        String menu = "MAIN_MENU";
        String action = "ENTER";

        // For the context violation scenario, we inject bad data based on the aggregate setup
        if ("session-context".equals(sid)) {
            menu = ""; // Invalid context
            action = "";
        }

        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sid, tid, menu, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Then Steps ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size(), "Expected exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        assertTrue(
                capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
                "Expected IllegalStateException or IllegalArgumentException, but got: " + capturedException.getClass().getName()
        );
    }
}