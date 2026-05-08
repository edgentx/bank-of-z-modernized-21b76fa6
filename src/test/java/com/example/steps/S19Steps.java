package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
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
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to create a basic valid aggregate for positive flows or modifications
    private TellerSessionAggregate createValidSession() {
        return new TellerSessionAggregate(UUID.randomUUID().toString());
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = createValidSession();
        // For a valid aggregate in a happy path context, we assume it's authenticated
        // and active. We explicitly set state here to ensure the test passes.
        aggregate.markAuthenticated(); // Domain method to set authenticated state
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // sessionId is implicitly handled by the aggregate instance we are using
        // But we ensure the command targets the correct ID.
        // Since the command holds the ID, we initialize the command object here or just rely on the aggregate ID.
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // We will construct the command in the 'When' step, but we validate the assumption here if needed.
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Same as above.
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        // Constructing the command with valid default data for the success scenario
        // or specific data if we had set it in previous steps (omitted for brevity in this pattern).
        String targetMenu = "MAIN_MENU_";
        String action = "ENTER";
        
        // For failure scenarios, we might want specific invalid inputs, but the Gherkin emphasizes state violations.
        // We assume the command payload itself is valid, but the aggregate state prevents execution.
        cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action);

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

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        // Do NOT mark authenticated. Default state is unauthenticated.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        aggregate.markAuthenticated();
        aggregate.simulateTimeout(); // Force timeout state
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesOperationalContext() {
        aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        aggregate.markAuthenticated();
        // Simulate a state where navigation is locked or invalid
        aggregate.lockNavigation();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException, 
            "Exception should be a domain error (IllegalStateException or IllegalArgumentException)");
    }
}