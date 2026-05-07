package com.example.steps;

import com.example.domain.shared.Command;
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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to reset state
    private void createNewAggregate(String id) {
        aggregate = new TellerSessionAggregate(id);
        repository.save(aggregate);
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        createNewAggregate("session-123");
        // Ensure it's in a valid state for the command
        aggregate.markAuthenticated();
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate construction in previous step
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Contextual setup, validated in execution
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Contextual setup, validated in execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        createNewAggregate("session-auth-fail");
        // Intentionally do NOT mark as authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        createNewAggregate("session-timeout-fail");
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        createNewAggregate("session-context-fail");
        aggregate.markAuthenticated();
        // Set a context that makes the navigation invalid (e.g. public user trying to go to admin)
        aggregate.setCurrentMenu("PUBLIC");
        // We will try to navigate to ADMIN in the When step
    }

    @When("the NavigateMenuCmd command is executed on invalid context")
    public void the_NavigateMenuCmd_command_is_executed_on_invalid_context() {
        try {
            // This command violates the invariant defined in the aggregate (Public -> Admin)
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ADMIN", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
