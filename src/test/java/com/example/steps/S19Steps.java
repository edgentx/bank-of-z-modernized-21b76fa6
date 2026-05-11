package com.example.steps;

import com.example.domain.aggregator.model.MenuNavigatedEvent;
import com.example.domain.aggregator.model.NavigateMenuCmd;
import com.example.domain.aggregator.model.TellerSessionAggregate;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final InMemoryTellerSessionRepository repository = new InMemoryTellerSessionRepository();
    
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure authenticated state for success case
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Do NOT mark authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago to simulate timeout (Timeout is 15 mins)
        aggregate.setLastActivity(Instant.now().minusSeconds(1200)); 
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-context");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu("MAIN_MENU"); // Actual state
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate creation in Given steps
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in When step
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in When step
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Default params for success/basic context
            String targetMenu = "ACCOUNT_DETAILS";
            String action = "ENTER";
            String expectedCurrentMenu = "MAIN_MENU";

            // Adjust params for specific scenarios based on state
            if (aggregate.id().equals("session-context")) {
                 // Trying to navigate assuming we are at LOGIN, but we are at MAIN_MENU
                 expectedCurrentMenu = "LOGIN";
            }
            
            // For the standard success case, ensure the aggregate matches the command's context expectation
            if (aggregate.id().equals("session-123")) {
                aggregate.setCurrentMenu("MAIN_MENU");
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action, expectedCurrentMenu);
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNT_DETAILS", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}