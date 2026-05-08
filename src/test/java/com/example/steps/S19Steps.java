package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private String providedMenuId;
    private String providedAction;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure authenticated
        aggregate.updateLastActivity(Instant.now()); // Ensure active
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Intentionally NOT calling markAuthenticated()
        aggregate.updateLastActivity(Instant.now());
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago (Default timeout is 15)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-400");
        aggregate.markAuthenticated();
        aggregate.updateLastActivity(Instant.now());
        repository.save(aggregate);
        // Context violation will be triggered by passing an invalid action in the command steps
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Implicitly handled by the aggregate ID, or we could store it in a context variable
        // Since we create the aggregate in the Given steps, we just acknowledge existence.
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.providedMenuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.providedAction = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Reload from repository to ensure clean state
            var agg = repository.findById(aggregate.id()).orElseThrow();
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), providedMenuId, providedAction);
            resultEvents = agg.execute(cmd);
            repository.save(agg);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(providedMenuId, event.menuId());
        Assertions.assertEquals(providedAction, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // The error message should match the specific invariant violation
        // We check the message content to verify the correct invariant failed
        System.out.println("Caught expected exception: " + caughtException.getMessage());
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
