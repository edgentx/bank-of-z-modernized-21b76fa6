package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: TellerSession Navigation.
 */
public class S19Steps {

    // Test Context (State)
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Givens

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate prior authentication event
        aggregate.markAuthenticated("teller-01");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate instantiation
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Implicitly handled in the When step by passing valid data
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Implicitly handled in the When step by passing valid data
    }

    // Violations Setup

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_auth() {
        String sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // DO NOT call markAuthenticated(). The aggregate defaults to unauthenticated.
        assertFalse(aggregate.isEnrolled()); // Hypothetical check, or rely on logic to fail
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01"); // Must be auth to get far enough to check timeout
        aggregate.markExpired(); // Set time to past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        // This scenario tests valid input against a potentially bad state, or bad input validation.
        // Based on the Domain logic implemented, this translates to providing invalid inputs (nulls)
        // or checking if the Aggregate allows invalid state transitions.
        // Here we create a valid aggregate but will feed it invalid context data in the 'When' step.
        String sessionId = "sess-context";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated("teller-01");
        aggregate.markInvalidContext(); // Setup specific state if needed
    }

    // Whens

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // We use valid inputs by default, unless the violation scenario implies specific bad inputs.
            // The 'Context' violation is best mapped to NPE/Invalid Argument in the execute method.
            String menuId = "MAIN_MENU";
            String action = "ENTER";

            // If we are in the 'Context Violation' scenario, let's pass invalid data to trigger the logic
            // (Simulating a state mismatch or invalid routing)
            if (aggregate.getCurrentMenuId() != null && aggregate.getCurrentMenuId().equals("INVALID_STATE_FOR_TEST")) {
                 // Simulating a bad context state or command mismatch
                 menuId = null; 
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | IllegalArgumentException | UnknownCommandException e) {
            capturedException = e;
        }
    }

    // Thens

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected a domain rule exception (IllegalStateException or IllegalArgumentException)"
        );
    }
}