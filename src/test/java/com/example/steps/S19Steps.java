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
        this.sessionId = "SESSION-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Setup valid state: authenticated, active, open
        this.aggregate.setAuthentication("TELLER-1", true);
        this.aggregate.setLastActivityAt(Instant.now());
        this.aggregate.setClosed(false);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "SESSION-INVALID-AUTH";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Explicitly unauthenticated
        this.aggregate.setAuthentication(null, false);
        this.aggregate.setLastActivityAt(Instant.now());
        this.aggregate.setClosed(false);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "SESSION-TIMEDOUT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated, but old activity timestamp
        this.aggregate.setAuthentication("TELLER-1", true);
        // Set activity to 20 minutes ago (default timeout is 15)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(20)));
        this.aggregate.setClosed(false);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.sessionId = "SESSION-BAD-CONTEXT";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Authenticated, fresh, but session is logically closed/terminated
        this.aggregate.setAuthentication("TELLER-1", true);
        this.aggregate.setLastActivityAt(Instant.now());
        this.aggregate.setClosed(true); // Violation: navigating a closed session
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled by the aggregate initialization in Given steps
        assertNotNull(this.sessionId);
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
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(this.resultEvents, "Events list should not be null");
        assertEquals(1, this.resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = this.resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(this.sessionId, navEvent.aggregateId());
        assertEquals(this.menuId, navEvent.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(this.capturedException, "An exception should have been thrown");
        // We expect IllegalStateException for Domain Errors in this pattern, or a custom DomainException
        assertTrue(
            this.capturedException instanceof IllegalStateException,
            "Expected IllegalStateException but got: " + this.capturedException.getClass().getSimpleName()
        );
        // Verify the message matches one of our invariant violations
        String message = this.capturedException.getMessage();
        assertTrue(
            message.contains("authenticated") || 
            message.contains("timeout") || 
            message.contains("context"),
            "Exception message should relate to the violated invariant. Got: " + message
        );
    }
}
