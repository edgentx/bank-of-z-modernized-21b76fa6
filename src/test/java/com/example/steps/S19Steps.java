package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Hydrating aggregate to a valid state (Authenticated & Active)
        // This simulates a past event stream leading to a valid session
        aggregate.markAuthenticated();
        aggregate.markActive();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Intentionally not marking authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.markAuthenticated();
        // Simulate timeout
        aggregate.markExpired(Instant.now().minusSeconds(60)); // Expired 1 min ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        this.sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        aggregate.markAuthenticated();
        aggregate.markActive();
        aggregate.freezeContext(); // Lock the context so navigation is impossible
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in the aggregate setup
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        this.menuId = "MAIN_MENU_01";
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(this.sessionId, this.menuId, this.action);
        try {
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(this.resultEvents);
        Assertions.assertEquals(1, this.resultEvents.size());
        Assertions.assertTrue(this.resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) this.resultEvents.get(0);
        Assertions.assertEquals(this.sessionId, event.aggregateId());
        Assertions.assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(this.thrownException);
        // Depending on the type of violation, it could be IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
