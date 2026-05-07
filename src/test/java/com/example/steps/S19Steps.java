package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.command.NavigateMenuCmd;
import com.example.domain.tellersession.event.MenuNavigatedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {
    private TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private String sessionId;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "session-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated(); // Ensure valid state
        repo.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Implicitly handled in command creation
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Implicitly handled in command creation
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, "MAIN_MENU", "ENTER");
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) aggregate.uncommittedEvents().get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "session-auth-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Intentionally NOT marking authenticated
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "session-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.expire();
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_operational_context() {
        this.sessionId = "session-ctx-fail";
        this.aggregate = new TellerSessionAggregate(sessionId);
        this.aggregate.markAuthenticated();
        this.aggregate.setOperational(false);
        repo.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
