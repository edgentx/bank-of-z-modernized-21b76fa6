package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a valid, authenticated, active session state prior to command
        aggregate.initializeStateForTesting("teller-456", Instant.now().plusSeconds(300), "MAIN_MENU");
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // sessionId already set in previous step
        Assertions.assertNotNull(sessionId);
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.menuId = "CUSTOMER_DETAILS";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            List<DomainEvent> events = aggregate.execute(cmd);
            // Events are automatically applied to the aggregate in execute()
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no error, but got: " + caughtException);
        List<DomainEvent> events = aggregate.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        Assertions.assertTrue(events.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) events.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(sessionId, event.aggregateId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set state where isAuthenticated is false
        aggregate.initializeStateForTesting(null, Instant.now().plusSeconds(300), "LOGIN_SCREEN");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Set state where lastAccessed is in the past
        aggregate.initializeStateForTesting("teller-456", Instant.now().minusSeconds(3600), "MAIN_MENU");
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_nav_state() {
        this.sessionId = "session-bad-context";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Put aggregate in a state where specific transition is invalid
        aggregate.initializeStateForTesting("teller-456", Instant.now().plusSeconds(300), "SCREEN_EXIT_CONFIRMATION");
        // Trying to navigate to a transaction screen from exit confirmation might be invalid logic
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Depending on implementation, this could be IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}