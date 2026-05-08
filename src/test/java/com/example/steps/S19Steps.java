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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String providedSessionId;
    private String providedMenuId;
    private String providedAction;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.providedSessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(providedSessionId);
        // Defaults for a valid aggregate:
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivityAt(Instant.now());
        this.aggregate.setCurrentContext("ROOT"); // Default valid context
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the setup above, but we ensure consistency
        assertNotNull(this.providedSessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.providedMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.providedAction = "ENTER";
    }

    // --- Violations ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.providedSessionId = "SESSION-UNAUTH";
        this.aggregate = new TellerSessionAggregate(providedSessionId);
        this.aggregate.setAuthenticated(false); // Violation
        this.aggregate.setLastActivityAt(Instant.now());
        this.providedMenuId = "MAIN_MENU";
        this.providedAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.providedSessionId = "SESSION-TIMEOUT";
        this.aggregate = new TellerSessionAggregate(providedSessionId);
        this.aggregate.setAuthenticated(true);
        // Set last activity to 20 minutes ago (assuming default timeout is 15)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        this.aggregate.setSessionTimeout(Duration.ofMinutes(15));
        this.providedMenuId = "MAIN_MENU";
        this.providedAction = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.providedSessionId = "SESSION-BAD-CONTEXT";
        this.aggregate = new TellerSessionAggregate(providedSessionId);
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivityAt(Instant.now());
        // Set context to something that invalidates navigation to the target (e.g., requires ROOT for ADMIN)
        this.aggregate.setCurrentContext("USER_HOME");
        this.providedMenuId = "ADMIN"; // Requires ROOT context based on aggregate logic
        this.providedAction = "ENTER";
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(providedSessionId, providedMenuId, providedAction);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Expected success, but got exception: " + caughtException);
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(providedSessionId, event.aggregateId());
        assertEquals(providedMenuId, event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException (Domain Error)");
    }
}
