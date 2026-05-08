package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        // Simulate authentication for the happy path
        aggregate.markAuthenticated();
        capturedException = null;
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate initialization in the previous step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Validated during execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Validated during execution
    }

    // --- Violation Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        // Create aggregate but do NOT mark authenticated (defaults to false)
        aggregate = new TellerSessionAggregate("session-auth-fail");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout-fail");
        aggregate.markAuthenticated();
        aggregate.forceInactive();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        // We interpret the invariant "Navigation state must accurately reflect the current operational context"
        // as validating that input is sane. If we receive garbage input (null/blank), it violates the invariant.
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.markAuthenticated();
    }

    // --- Execution ---

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        // Attempt to execute. Since steps differ slightly, we infer intent based on context or specific step chains.
        // The Happy Path:
        if (aggregate.id().equals("session-123")) {
             NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
             try {
                 resultEvents = aggregate.execute(cmd);
             } catch (Exception e) {
                 capturedException = e;
             }
        }
        // The Authentication Failure Path:
        else if (aggregate.id().equals("session-auth-fail")) {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-auth-fail", "MAIN_MENU", "ENTER");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (Exception e) {
                capturedException = e;
            }
        }
        // The Timeout Failure Path:
        else if (aggregate.id().equals("session-timeout-fail")) {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-timeout-fail", "MAIN_MENU", "ENTER");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (Exception e) {
                capturedException = e;
            }
        }
        // The Nav State Failure Path (simulating invalid context):
        else if (aggregate.id().equals("session-nav-fail")) {
            // Send a command with invalid/blank menuId to trigger the domain logic enforcing clean state
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-nav-fail", "", "ENTER");
            try {
                resultEvents = aggregate.execute(cmd);
            } catch (Exception e) {
                capturedException = e;
            }
        }
    }

    // --- Outcomes ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents, "Result events should not be null");
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", event.type());
        assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In DDD, invariant violations usually throw IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}