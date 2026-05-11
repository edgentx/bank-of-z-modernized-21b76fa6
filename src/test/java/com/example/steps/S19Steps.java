package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<? extends DomainException> events;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Setup valid state
    }
    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by command creation in 'When'
    }
    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled by command creation in 'When'
    }
    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled by command creation in 'When'
    }

    // Scenario 2: Auth Violation
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // intentionally not calling markAuthenticated()
    }

    // Scenario 3: Timeout Violation
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-stale");
        aggregate.markAuthenticated(); // Is logged in
        aggregate.markStale(); // But timed out
    }

    // Scenario 4: Context Violation
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-bad-context");
        aggregate.markAuthenticated();
        aggregate.setContext("MENU_HOME");
        // The specific logic for 'ILLEGAL_DESTINATION' is handled in the Aggregate execute method
    }

    // --- Execution ---
    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Using a "valid" menuId by default, unless overridden by scenario context logic
            // For Scenario 4 (Context Violation), we trigger the violation by sending the specific bad ID
            String menuId = "MENU_DASHBOARD";
            if (aggregate.id().equals("session-bad-context")) {
                menuId = "ILLEGAL_DESTINATION"; // Triggers context violation in Aggregate
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), menuId, "ENTER");
            events = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Outcomes ---
    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(events);
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("menu.navigated", events.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // We verify it's a logical domain rule violation (IllegalStateException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
