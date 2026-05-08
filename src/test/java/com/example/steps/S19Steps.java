package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.TellerSession;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup: Create a valid authenticated session with standard activity
        aggregate = new TellerSessionAggregate("SESSION-001");
        // Manually constructing state to bypass init command for unit test isolation
        aggregate.initializeState(
            "TELLER-001", 
            Instant.now(), 
            Duration.ofHours(1) // Timeout 1 hour
        );
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-INVALID-AUTH");
        // Simulating a session state that is not authenticated (e.g., guest session)
        aggregate.initializeState(
            null, // No teller ID -> Not Authenticated
            Instant.now(),
            Duration.ofHours(1)
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        // Simulating a session that timed out (last activity 2 hours ago)
        aggregate.initializeState(
            "TELLER-001",
            Instant.now().minus(Duration.ofHours(2)), // Last activity 2h ago
            Duration.ofMinutes(30) // Configured timeout 30m
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-INVALID-STATE");
        aggregate.initializeState(
            "TELLER-001",
            Instant.now(),
            Duration.ofHours(1)
        );
        // Simulating invalid state: Currently locked or in transaction that forbids menu navigation
        aggregate.setState(TellerSession.SessionState.LOCKED); 
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by aggregate construction in @Given steps
        // If we were testing a handler/repo, we would capture the ID here
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Context for the command creation
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context for the command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Create command with valid payload
            NavigateMenuCmd cmd = new NavigateMenuCmd("SESSION-001", "MAIN_MENU", "SELECT_OPTION_1");
            // Note: Session ID in command must match aggregate ID for the test context of "valid"
            // Adjusting cmd sessionId based on current aggregate setup
            if (aggregate.id().equals("SESSION-TIMEOUT")) {
                cmd = new NavigateMenuCmd("SESSION-TIMEOUT", "MAIN_MENU", "SELECT");
            } else if (aggregate.id().equals("SESSION-INVALID-AUTH")) {
                cmd = new NavigateMenuCmd("SESSION-INVALID-AUTH", "MAIN_MENU", "SELECT");
            } else if (aggregate.id().equals("SESSION-INVALID-STATE")) {
                cmd = new NavigateMenuCmd("SESSION-INVALID-STATE", "MAIN_MENU", "SELECT");
            }

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event type mismatch");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it's a domain-related error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected domain logic exception"
        );
    }
}
