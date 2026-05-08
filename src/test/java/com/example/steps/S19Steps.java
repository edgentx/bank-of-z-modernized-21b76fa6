package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S19Steps {

    @Autowired
    private TellerSessionRepository repository;

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private String resultMenuId;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated("teller-123"); // Ensure authenticated
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Do not mark authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.markAuthenticated("teller-123");
        aggregate.expireSession(); // Simulate timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.markAuthenticated("teller-123");
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate construction in previous steps
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the When step via the command
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step via the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Use "MAIN_MENU" unless we are testing the context violation scenario
            String action = aggregate.id().equals("session-4") ? "INVALID_CONTEXT" : "ENTER";
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", action);
            aggregate.execute(cmd);
            repository.save(aggregate);
            resultMenuId = aggregate.getCurrentMenuId();
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(aggregate.uncommittedEvents());
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertEquals("menu.navigated", aggregate.uncommittedEvents().get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}