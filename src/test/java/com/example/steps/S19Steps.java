package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
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
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a previous session initiation to satisfy invariants
        // (In a real flow, InitiateSessionCmd would have been called)
        aggregate.markAsAuthenticated();
        aggregate.updateLastActivity(Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId already set in context
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

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Deliberately not marking as authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated();
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        this.sessionId = "session-bad-state";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAsAuthenticated();
        aggregate.updateLastActivity(Instant.now());
        // Simulate a state where navigation is invalid or locked
        aggregate.lockNavigation();
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        Command cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(this.capturedException, "Should not have thrown an exception");
        assertNotNull(this.resultEvents);
        assertFalse(this.resultEvents.isEmpty(), "Events list should not be empty");
        
        DomainEvent event = this.resultEvents.get(0);
        assertEquals("tellersession.menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(this.capturedException, "Expected a domain error exception");
        // Invariants enforced via IllegalStateException or IllegalArgumentException
        assertTrue(
            this.capturedException instanceof IllegalStateException || 
            this.capturedException instanceof IllegalArgumentException
        );
    }
}
