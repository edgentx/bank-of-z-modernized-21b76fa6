package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private String currentSessionId;
    private String currentMenuId;
    private String currentAction;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        currentSessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Setup valid state: authenticated and active
        aggregate.markAuthenticated("teller-123");
        aggregate.setCurrentMenu("MAIN_MENU");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        currentSessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(currentSessionId);
        // Intentionally do not mark as authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        currentSessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-123");
        // Force expiration
        aggregate.markExpired();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        currentSessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(currentSessionId);
        aggregate.markAuthenticated("teller-123");
        // Set to a blocked state to violate context validity
        aggregate.setCurrentMenu("BLOCKED");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId is already set in the Given steps
        Assertions.assertNotNull(currentSessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        currentMenuId = "ACCOUNT_DETAILS";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        currentAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Reload from repo to simulate standard aggregate flow
            TellerSessionAggregate loadedAggregate = repository.findById(currentSessionId)
                    .orElseThrow(() -> new IllegalStateException("Aggregate not found"));

            NavigateMenuCmd cmd = new NavigateMenuCmd(currentSessionId, currentMenuId, currentAction);
            resultEvents = loadedAggregate.execute(cmd);

            // Save changes
            repository.save(loadedAggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException.getMessage());
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Verify it's an IllegalStateException or similar domain error
        Assertions.assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException, got " + caughtException.getClass().getSimpleName());
    }
}