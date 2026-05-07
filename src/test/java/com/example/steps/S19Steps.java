package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup: Authenticated, Active, Valid Context
        aggregate = new TellerSessionAggregate("SESSION-1");
        aggregate.initialize("TELLER-101", "BRANCH-01"); // Sets authenticated = true, lastActivity = now
        // Force context to be valid
        aggregate.setContextState("MAIN_MENU"); 
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // ID is set in constructor, assumed valid
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Value will be provided in the When block
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Value will be provided in the When block
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Standard navigation command
            NavigateMenuCmd cmd = new NavigateMenuCmd("SESSION-1", "DEPOSIT_SCREEN", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // --- Scenarios for Violations ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("SESSION-99");
        aggregate.initialize("TELLER-102", "BRANCH-01");
        aggregate.setAuthenticated(false); // Force violation
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("SESSION-88");
        aggregate.initialize("TELLER-103", "BRANCH-01");
        // Simulate timeout: set last activity to 31 minutes ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("SESSION-77");
        aggregate.initialize("TELLER-104", "BRANCH-01");
        aggregate.setContextState("INVALID_STATE_BLOCK"); // Force violation context
    }

    // --- Verification ---

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("SESSION-1", event.aggregateId());
        Assertions.assertEquals("DEPOSIT_SCREEN", event.targetMenuId());
        Assertions.assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Should be one of the IllegalState or IllegalArgument exceptions defined in the aggregate
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
