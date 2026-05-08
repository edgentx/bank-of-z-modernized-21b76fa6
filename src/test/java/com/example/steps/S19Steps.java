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

/**
 * Cucumber Steps for S-19: NavigateMenuCmd
 * Uses in-memory aggregates to validate business rules.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "SESSION-" + System.currentTimeMillis();
        aggregate = new TellerSessionAggregate(sessionId);
        // By default, we simulate an authenticated, active session for the happy path
        aggregate.markAuthenticated("TELLER-001");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is already initialized in the aggregate setup
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT authenticate. Default is false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-002");
        // Force the timestamp to be old
        aggregate.markStale();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        sessionId = "SESSION-INVALID";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("TELLER-003");
        // Invalidate the internal state flag
        aggregate.invalidateState();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain exception to be thrown");
        // We verify it's a specific business logic exception type (IllegalStateException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
