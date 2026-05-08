package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // --- Scenario 1: Success ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // Simulate prior login to ensure state is valid
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionInitializedEvent(id, "teller123", java.time.Instant.now()));
        // Initialize state manually for test simplicity (or add a LoginCmd)
        aggregate.markStateForTest(TellerSession.State.AUTHENTICATED);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate creation in previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Will be passed in the command
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Will be passed in the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        String menuId = "MAIN_MENU";
        String action = "ENTER";
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
    }

    // --- Scenario 2: Authentication Required ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        // State is initialized but NOT authenticated
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionInitializedEvent(id, "teller123", java.time.Instant.now()));
        aggregate.markStateForTest(TellerSession.State.UNAUTHENTICATED);
    }

    // Reuse When from above

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Should have thrown an exception");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Should be an IllegalStateException");
    }

    // --- Scenario 3: Session Timeout ---

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionInitializedEvent(id, "teller123", java.time.Instant.now()));
        aggregate.markStateForTest(TellerSession.State.TIMED_OUT);
    }

    // --- Scenario 4: Navigation State Context ---

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.applyEvent(new com.example.domain.tellersession.model.SessionInitializedEvent(id, "teller123", java.time.Instant.now()));
        aggregate.markStateForTest(TellerSession.State.LOCKED);
    }

}
