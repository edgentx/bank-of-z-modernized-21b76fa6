package com.example.steps;

import com.example.domain.teller.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Simulate a valid session
        aggregate = new TellerSessionAggregate("SESSION-1");
        // Initialize state (bypassing command for setup purposes)
        aggregate.initializeForTest("TELLER-1", Instant.now().minusSeconds(60));
        aggregate.markAuthenticated(); // Set authenticated = true
        aggregate.updateCurrentMenu("MAIN_MENU"); // Set initial valid context
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Will be passed in the command
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be passed in the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Default happy path values if not overridden by violation setup
            String targetMenu = "ACCOUNT_DETAILS_MENU";
            String action = "ENTER";
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action, Instant.now());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("ACCOUNT_DETAILS_MENU", event.targetMenuId());
    }

    // --- Violations ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-UNAUTH");
        aggregate.initializeForTest("TELLER-1", Instant.now().minusSeconds(60));
        // Intentionally NOT calling markAuthenticated(). Defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-TIMEOUT");
        // Initialize with a timestamp WAY in the past (simulating timeout)
        aggregate.initializeForTest("TELLER-1", Instant.now().minus(Duration.ofHours(2)));
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("SESSION-INVALID-STATE");
        aggregate.initializeForTest("TELLER-1", Instant.now().minusSeconds(60));
        aggregate.markAuthenticated();
        // Do not set a valid current menu (defaults to null/empty)
        // OR explicitly set it to a state that implies no navigation is possible (e.g., LOCKED)
        aggregate.updateCurrentMenu("LOCKED_STATE");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // In DDD, usually we throw IllegalStateException or IllegalArgumentException for invariants
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // --- Helper overrides for When based on context ---
    // Note: In a real Cucumber setup, we might use Scenario Context or DataTables.
    // Here we rely on the aggregate state to trigger the failure, the command inputs can be generic.
}
