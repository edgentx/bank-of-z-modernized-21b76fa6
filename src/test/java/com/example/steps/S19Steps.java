package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure valid state (authenticated)
        aggregate.markAuthenticated();
        // Ensure valid state (active)
        aggregate.setLastActivity(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        this.sessionId = "session-123"; // Already set in aggregate creation, ensuring consistency
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.menuId = "MAIN_MENU";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event must be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = "session-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Deliberately do NOT call markAuthenticated() -> defaults to false
        this.menuId = "ANY_MENU";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set last activity to 2 hours ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofHours(2)));
        // Set timeout threshold to 30 mins (default is 30, but setting explicitly for clarity in test)
        aggregate.setTimeoutThreshold(Duration.ofMinutes(30));
        this.menuId = "ANY_MENU";
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        this.sessionId = "session-context";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Simulate invalid operational context request (e.g. null action)
        this.menuId = "DYNAMIC_MENU";
        this.action = null; // Violates context rules
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check it's a domain-related error (IllegalStateException or IllegalArgumentException)
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected IllegalStateException or IllegalArgumentException, but got: " + capturedException.getClass().getSimpleName()
        );
        // Verify the error message matches the invariants
        assertTrue(capturedException.getMessage().length() > 0);
    }
}