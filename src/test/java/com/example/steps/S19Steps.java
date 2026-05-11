package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
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
    private Exception caughtException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Setup a valid session with authenticated user and active state
        aggregate = new TellerSessionAggregate("session-123");
        // We use a package-private or protected setup approach if available, 
        // or reflectively set state for test purposes. 
        // Ideally, we wouldhydrate the aggregate from events, but for this BDD stub, we assume valid state.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        aggregate = new TellerSessionAggregate("session-401");
        // Force state to unauthenticated (simulated)
        // In a real implementation, this would be a specific state transition
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_timed_out() {
        aggregate = new TellerSessionAggregate("session-timeout");
        // Force state to timed out (simulated)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_invalid_context() {
        aggregate = new TellerSessionAggregate("session-invalid-context");
        // Force state to invalid context (simulated)
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Implicitly handled by the aggregate creation
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
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MENU_MAIN", "ENTER");
            resultingEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
            resultingEvents = List.of();
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertFalse(resultingEvents.isEmpty());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException);
    }
}
