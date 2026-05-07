package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String providedSessionId;
    private String providedMenuId;
    private String providedAction;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    private final InMemoryTellerSessionRepository repo = new InMemoryTellerSessionRepository();

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Setup valid state for success scenario
        this.aggregate.hydrate("teller-1", true, "MAIN_MENU", Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.providedSessionId = "session-123";
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.providedMenuId = "ACCOUNT_SUMMARY";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.providedAction = "SELECT";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(providedSessionId, providedMenuId, providedAction);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(providedSessionId, event.aggregateId());
        Assertions.assertEquals(providedMenuId, event.targetMenuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-401");
        // Explicitly unauthenticated
        this.aggregate.hydrate(null, false, "LOGIN", Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-408");
        // Authenticated, but last activity was 20 minutes ago (timeout is 15)
        this.aggregate.hydrate("teller-1", true, "MAIN_MENU", Instant.now().minusSeconds(1200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.aggregate = new TellerSessionAggregate("session-400");
        // Authenticated, active, but trying to jump from Main to Admin (see aggregate logic)
        this.aggregate.hydrate("teller-1", true, "MAIN_MENU", Instant.now());
        this.providedMenuId = "ADMIN_PANEL"; // Invalid transition per aggregate logic
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected exception but command succeeded");
        // Check it's one of our expected domain errors
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected domain error, got: " + capturedException.getClass().getName()
        );
        
        // Optionally verify message content based on specific invariants
        Assertions.assertTrue(capturedException.getMessage() != null && !capturedException.getMessage().isBlank());
    }
}