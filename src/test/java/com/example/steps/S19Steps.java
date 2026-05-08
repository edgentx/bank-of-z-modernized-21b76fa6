package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate session initialization to make it valid
        aggregate.initializeSession("teller-1", "MAIN_MENU");
        aggregate.markActive(); // Ensure not timed out
        aggregate.markAuthenticated(); // Ensure authenticated
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the first Given
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "ACCOUNT_MENU";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        sessionId = "sess-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initializeSession("teller-1", "MAIN_MENU");
        aggregate.markActive();
        // Deliberately do not mark as authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        sessionId = "sess-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initializeSession("teller-1", "MAIN_MENU");
        aggregate.markAuthenticated();
        aggregate.markTimedOut(); // Simulate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        sessionId = "sess-bad-ctx";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.initializeSession("teller-1", "MAIN_MENU");
        aggregate.markAuthenticated();
        aggregate.markActive();
        // Simulate being in a state where navigation is blocked (e.g. CICS link down)
        aggregate.breakContext();
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // In this domain, we use standard IllegalStateExceptions or IllegalArgumentExceptions as domain errors
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Helper for scenarios to hook into specific Given setups that need to modify the aggregate post-construction
    // Note: The aggregate methods used below (initializeSession, markActive, etc.) would be package-private or protected in reality,
    // but for this test structure we assume they exist or we rely on the specific setup logic in the aggregate constructor/factory.
    // For the sake of this test file, we will assume the Aggregate supports these testing hooks or we perform the logic directly.
    
    // Since we are mocking the internal state for BDD, we can use reflection or specific setup methods if available.
    // Below assumes the Aggregate has these methods for state manipulation (simulating persistence load)
}
