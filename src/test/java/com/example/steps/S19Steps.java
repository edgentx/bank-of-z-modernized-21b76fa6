package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellsession.model.NavigateMenuCmd;
import com.example.domain.tellsession.model.TellerSessionAggregate;
import com.example.domain.tellsession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellsession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller123"); // Setup: Make it authenticated
        aggregate.updateLastActivity(Instant.now()); // Setup: Make it active
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate setup
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in command execution
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "SELECT");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally not calling markAuthenticated
        aggregate.updateLastActivity(Instant.now());
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller123");
        // Set last activity to 1 hour ago to simulate timeout (assuming threshold is less)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(1)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller123");
        aggregate.updateLastActivity(Instant.now());
        aggregate.lockNavigation(); // Put it in a state where navigation is invalid
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected exception but none was thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException || 
                              caughtException instanceof IllegalArgumentException);
    }
}
