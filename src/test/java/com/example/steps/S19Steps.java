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
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123", Instant.now());
        aggregate.markAuthenticated("teller-456");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled in the aggregate constructor in the previous step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Will be handled in the When step constructing the command
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be handled in the When step constructing the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        assertEquals("menu.navigated", resultEvents.get(0).type());
        assertEquals("MAIN_MENU", ((MenuNavigatedEvent) resultEvents.get(0)).menuId());
    }

    // Negative Scenarios

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth", Instant.now());
        // Deliberately not calling markAuthenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout", Instant.now().minus(Duration.ofHours(1)));
        aggregate.markAuthenticated("teller-456");
        aggregate.setTimeoutThreshold(Duration.ofMinutes(30)); // Ensure threshold is passed
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        aggregate = new TellerSessionAggregate("session-bad-nav", Instant.now());
        aggregate.markAuthenticated("teller-456");
        // The violation here is simulated by sending a bad command (null menuId) in the When step
    }

    // Overriding When for negative tests if specific context is needed, but the generic When works.
    // We just need to pass specific params to the command for the last scenario.
    
    // We can use a specific hook or just logic in the step. To keep it clean, we'll check the scenario context.
    // However, for simplicity in this generated code, let's assume the generic "When" sends valid data 
    // EXCEPT for the last case. We will handle the last case by checking the aggregate ID or adding a specific When.
    
    // Let's refine the 'When' logic via context or specific steps.
    
    @When("the NavigateMenuCmd command is executed with invalid context")
    public void the_navigate_menu_cmd_command_is_executed_invalid() {
         try {
            // This satisfies the "Navigation state... context" violation (null menuId)
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-bad-nav", null, "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
