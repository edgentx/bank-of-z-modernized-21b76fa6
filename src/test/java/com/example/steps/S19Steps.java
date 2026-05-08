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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "session-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Default setup: authenticated and active
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = "session-unauth";
        aggregate = new TellerSessionAggregate(sessionId);
        // Do NOT authenticate
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated(); // Authenticated, but...
        aggregate.expireSession();      // ...set last activity to the past
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        String sessionId = "session-inactive";
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        aggregate.deactivate(); // Set state to inactive
    }

    @And("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is implicitly handled by the aggregate construction in the Given steps.
        // We verify the ID matches if needed, but for this BDD, the existence of the aggregate satisfies this.
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Parameters are provided in the 'When' step.
        // This step acts as a documentation placeholder in the Gherkin flow.
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Parameters are provided in the 'When' step.
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Expected success but got exception: " + capturedException);
        assertNotNull(resultEvents, "Result events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");

        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");

        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(aggregate.id(), navEvent.aggregateId());
        assertEquals("MAIN_MENU", navEvent.menuId());
        assertEquals("ENTER", navEvent.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We expect IllegalStateException based on our implementation logic
        assertTrue(capturedException instanceof IllegalStateException ||
                   capturedException instanceof IllegalArgumentException ||
                   capturedException instanceof UnknownCommandException,
                   "Exception should be a domain error (IllegalState/Argument/UnknownCmd), but was: " + capturedException.getClass().getSimpleName());
        
        // Verify no events were committed
        assertTrue(aggregate.uncommittedEvents().isEmpty(), "No events should be recorded when command is rejected");
    }
}
