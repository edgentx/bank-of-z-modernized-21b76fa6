package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.model.MenuNavigatedEvent;
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
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        // Simulate initialization via constructor or internal hydration
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Hydrate state to make it valid for navigation (Authenticated, Active)
        aggregate.markAuthenticated();
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = "session-invalid-auth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // State defaults to NOT authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = "session-invalid-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Simulate timeout (e.g. last active time was > 30 mins ago)
        aggregate.forceLastActivityTime(Instant.now().minus(Duration.ofMinutes(35)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        String sessionId = "session-invalid-context";
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.markAuthenticated();
        // Set invalid context: no screen available or locked
        aggregate.markContextInvalid();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Session ID is implicitly handled by the aggregate instance creation above
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When block command construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When block command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd("MENU_MAIN", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Expected events list");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception");
        // In a real app, we might catch a custom DomainException, but RuntimeException works for this test
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
