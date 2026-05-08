package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute NavigateMenuCmd

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "SESSION-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Ensure it is authenticated for success scenario
        aggregate.authenticate("TELLER-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in aggregate creation
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in command creation
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command creation
    }

    // Scenario: Rejected - Authenticated

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "SESSION-UNAUTH";
        aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT calling authenticate()
    }

    // Scenario: Rejected - Timeout

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "SESSION-TIMEOUT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("TELLER-02");
        // Set last activity to 20 minutes ago
        aggregate.setLastActivity(Instant.now().minus(20, java.time.temporal.ChronoUnit.MINUTES));
    }

    // Scenario: Rejected - Operational Context

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        String sessionId = "SESSION-BAD-CONTEXT";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.authenticate("TELLER-03");
        // Set context to Maintenance
        aggregate.setOperationalContext("MAINTENANCE");
    }

    // Execution

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Default parameters for navigation
            String targetMenu = "MAIN_MENU";
            // If context is MAINTENANCE, try to go somewhere invalid to trigger the context error
            if ("MAINTENANCE".equals(aggregate.getCurrentMenuId()) || "SESSION-BAD-CONTEXT".equals(aggregate.id())) {
                 targetMenu = "TX_DEPOSIT"; // Invalid during maintenance
            }
            
            // Note: The aggregate logic checks "MAINTENANCE" context and rejects anything non-admin
            // We use TX_DEPOSIT to trigger the rejection
            Command cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    // Then

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}