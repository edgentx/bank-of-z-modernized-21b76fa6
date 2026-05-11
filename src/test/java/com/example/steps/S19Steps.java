package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellermenu.model.MenuNavigatedEvent;
import com.example.domain.tellermenu.model.NavigateMenuCmd;
import com.example.domain.tellermenu.model.TellerSessionAggregate;
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
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Simulate a valid authenticated session
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivity(Instant.now());
        this.aggregate.setOperationMode("NORMAL");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-401";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(false); // Violation
        this.aggregate.setLastActivity(Instant.now());
        this.aggregate.setOperationMode("NORMAL");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-408";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        // Violation: Activity was 30 mins ago (assuming 15 min timeout)
        this.aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(30)));
        this.aggregate.setOperationMode("NORMAL");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        this.sessionId = "session-409";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivity(Instant.now());
        // Violation: Discrepancy between state and requested menu/action
        this.aggregate.setOperationMode("LOCKED"); // Cannot navigate to normal menu
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // sessionId is usually set in the aggregate constructor or given steps
        assertNotNull(this.aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "MENU_WITHDRAWAL";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
            this.resultingEvents = this.aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.capturedException = e;
        } catch (UnknownCommandException e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(this.capturedException, "Should not have thrown an exception");
        assertNotNull(this.resultingEvents);
        assertFalse(this.resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        assertEquals(this.menuId, event.menuId());
        assertEquals(this.action, event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(this.capturedException, "Expected an exception to be thrown");
        assertTrue(
            this.capturedException instanceof IllegalStateException || 
            this.capturedException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException), but got: " + this.capturedException.getClass().getSimpleName()
        );
    }
}
