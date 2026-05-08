package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.InitiateSessionCmd;
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
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initiate to ensure valid state
        aggregate.execute(new InitiateSessionCmd(sessionId, "teller-1", "term-1"));
        aggregate.clearEvents(); // Clear init events to focus on test scenario
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in aggregate creation
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be provided in the When step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be provided in the When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("sess-123", "MAIN_MENU", "OPEN");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("sess-unauth");
        // Manually force state to be unauthenticated without issuing Initiate command
        aggregate.markUnauthenticated();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.execute(new InitiateSessionCmd(sessionId, "teller-1", "term-1"));
        aggregate.markStale(); // Helper to set lastActivity far in the past
        aggregate.clearEvents();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        String sessionId = "sess-bad-ctx";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.execute(new InitiateSessionCmd(sessionId, "teller-1", "term-1"));
        aggregate.clearEvents();
    }

    @When("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        // Just acts as a placeholder for Then logic in BDD flow
    }

    @Then("the command is rejected with a domain error")
    public void validateCommandRejected() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // In real DDD, this would be a specific DomainError, but here we rely on RuntimeException/IllegalStateException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
