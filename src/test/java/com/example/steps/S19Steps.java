package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to create a valid session for positive and context-violation scenarios
    private TellerSessionAggregate createValidSession() {
        String sessionId = "SESSION-" + UUID.randomUUID();
        TellerSessionAggregate agg = new TellerSessionAggregate(sessionId);
        // Seed state to bypass authentication checks for non-auth tests
        agg.applySeed(new SessionInitializedEvent(sessionId, "TELLER-1", java.time.Instant.now()));
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = createValidSession();
        Assertions.assertNotNull(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        // Create a session that has NOT been initialized (no authentication)
        String sessionId = "SESSION-" + UUID.randomUUID();
        aggregate = new TellerSessionAggregate(sessionId);
        // State is empty/not authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatIsTimedOut() {
        aggregate = createValidSession();
        // Manually force a timeout state for simulation
        aggregate.applySeed(new SessionTerminatedEvent(aggregate.id(), "SESSION_TIMEOUT", java.time.Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateWithInvalidContext() {
        aggregate = createValidSession();
        // Force state to something invalid for the menu navigation logic (e.g. Locked)
        aggregate.applySeed(new SessionLockedEvent(aggregate.id(), "SECURITY_FREEZE", java.time.Instant.now()));
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate creation
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context loaded for command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context loaded for command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Using hardcoded valid values for the positive path and non-violating paths
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("MAIN_MENU", event.getMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected a domain error/exception");
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // In-memory repository implementation for testing
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        // Stub implementation, not used for command execution in this step style
        // but required if the aggregate attempted to load itself (not done here).
    }
}