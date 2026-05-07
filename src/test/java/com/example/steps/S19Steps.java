package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {
    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Ensure valid state for positive case
        aggregate.markAuthenticated("teller-1");
        aggregate.setLastActivityAt(Instant.now());
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is implicit in the aggregate creation above
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Used in the 'When' step
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Used in the 'When' step
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
            repo.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Explicitly DO NOT authenticate
        aggregate.deAuthenticate();
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-1");
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200));
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated("teller-1");
        aggregate.setLastActivityAt(Instant.now());
        // We will pass invalid data in the command step for this scenario
        // or we could model the aggregate state as invalid. 
        // Here, the prompt implies the aggregate state is the issue, but Navigation Context validation is usually input/command driven.
        // We will simulate this by assuming the standard execution path, but we'll catch the error.
        repo.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Hook for context violation - overriding When for specific context handling
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void theNavigateMenuCmdCommandIsExecutedWithInvalidContext() {
        // Passing an empty menuId to trigger the "accurately reflect operational context" error
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-bad-context", "", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}