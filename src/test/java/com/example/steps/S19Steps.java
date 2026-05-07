package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.command.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        // Default valid state: authenticated and active
        aggregate.markAuthenticated(); 
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate construction in previous step
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the 'When' step command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the 'When' step command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Refresh aggregate from repo to simulate persistence/behavior
            Optional<TellerSessionAggregate> loaded = repository.findById(aggregate.id());
            Assertions.assertTrue(loaded.isPresent(), "Aggregate should exist in repo");
            
            TellerSessionAggregate session = loaded.get();
            NavigateMenuCmd cmd = new NavigateMenuCmd(session.id(), "MAIN_MENU", "ENTER");
            
            resultingEvents = session.execute(cmd);
            repository.save(session); // Save state change
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertEquals("menu.navigated", resultingEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String id = "session-unauth";
        aggregate = new TellerSessionAggregate(id);
        // Explicitly NOT calling markAuthenticated()
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated(); // Valid auth
        aggregate.markExpired();        // But timed out
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        String id = "session-context";
        aggregate = new TellerSessionAggregate(id);
        aggregate.markAuthenticated();
        aggregate.setContext("MAIN_MENU"); // Current context
        repository.save(aggregate);
        // The 'When' step below attempts to navigate to the same context with ENTER action,
        // triggering the 'already at menu' error defined in the aggregate.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Usually domain errors are IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error, got: " + caughtException.getClass().getSimpleName()
        );
    }
}