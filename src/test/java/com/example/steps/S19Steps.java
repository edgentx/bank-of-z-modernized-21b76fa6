package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
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
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.initialize("teller-1", Instant.now());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Aggregate is created but not initialized (authenticated)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        // Initialize with a timestamp well beyond the timeout window (e.g., 30 minutes ago)
        aggregate.initialize("teller-1", Instant.now().minus(Duration.ofMinutes(35)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-409");
        aggregate.initialize("teller-1", Instant.now());
        // Simulate a state where the teller is in a mode that disallows generic navigation
        // e.g., transaction-in-progress
        aggregate.beginTransaction();
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate construction setup
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When step via command construction
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd("MENU_DEPOSIT", "ENTER");
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MENU_DEPOSIT", event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect a specific runtime exception (IllegalStateException or IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_duplicate() {
        // Cucumber allows duplicate step definitions as long as they are identical or logically equivalent
        // Delegating to the primary check
        the_command_is_rejected_with_a_domain_error();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_triplicate() {
        // Delegating to the primary check
        the_command_is_rejected_with_a_domain_error();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error_quad() {
        // Delegating to the primary check
        the_command_is_rejected_with_a_domain_error();
    }
}
