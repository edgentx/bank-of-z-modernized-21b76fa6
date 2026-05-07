package com.example.steps;

import com.example.domain.shared.Command;
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
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        sessionId = "sess-123";
        // Configure default valid state
        aggregate = new TellerSessionAggregate(sessionId)
                .withAuthentication(true)
                .withContextValidity(true)
                .withLastActivity(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId initialized in previous step
        assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId)
                .withAuthentication(false) // Violation
                .withContextValidity(true)
                .withLastActivity(Instant.now());
        
        menuId = "SECURE_MENU";
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        sessionId = "sess-timeout";
        // Set last activity to 20 minutes ago (assuming default 15 min timeout)
        Instant past = Instant.now().minus(Duration.ofMinutes(20));
        
        aggregate = new TellerSessionAggregate(sessionId)
                .withAuthentication(true)
                .withContextValidity(true)
                .withLastActivity(past); // Violation
        
        menuId = "MAIN_MENU";
        action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContextValidity() {
        sessionId = "sess-invalid-ctx";
        aggregate = new TellerSessionAggregate(sessionId)
                .withAuthentication(true)
                .withContextValidity(false) // Violation
                .withLastActivity(Instant.now());
        
        menuId = "ORPHANED_MENU";
        action = "ENTER";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check if it's the right kind of error (State/IllegalArgument)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
