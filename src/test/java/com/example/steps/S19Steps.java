package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.InvalidTellerSessionStateException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Helper to create a valid base aggregate
    private TellerSessionAggregate createValidSession() {
        String id = UUID.randomUUID().toString();
        TellerSessionAggregate agg = new TellerSessionAggregate(id);
        // Force state to valid by simulating a login event logic or direct state hydration
        // For this unit-test level step, we assume the aggregate can be initialized or hydrated.
        // Since we don't have a LoginCmd in this story, we rely on the aggregate constructor
        // or a hydration helper to set up the 'authenticated' state.
        
        // Based on the acceptance criteria, we need to test "valid TellerSession aggregate".
        // We will assume a standard constructor that initializes state to authenticated/fresh 
        // or use reflection/setters if available. Since AggregateRoot is the base, 
        // and we are testing command execution, we will assume the aggregate has a mechanism
        // to be in a valid state (e.g. constructor)
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = createValidSession();
        // Ensure it is authenticated and not timed out for the happy path
        this.aggregate.authenticate(); // Assumes method or similar hydration
        this.aggregate.updateLastActivity(Instant.now());
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by the aggregate creation in the previous step
        // We just verify it's not null
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Context for the command creation in 'When'
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Context for the command creation in 'When'
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate(UUID.randomUUID().toString());
        // Do NOT authenticate. The aggregate should be in a state where isAuthenticated is false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = createValidSession();
        this.aggregate.authenticate();
        // Set last activity to a long time ago
        this.aggregate.updateLastActivity(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_context() {
        this.aggregate = createValidSession();
        this.aggregate.authenticate();
        // The aggregate needs to be in a state where the action/menuId is invalid for the current context.
        // We'll use a flag or specific state method to simulate this invariant violation.
        this.aggregate.setContextRestricted(true);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        String menuId = "MAIN_MENU";
        String action = "ENTER";
        
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), menuId, action);
            resultEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("MAIN_MENU", event.menuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // We expect an IllegalArgumentException or IllegalStateException, or a specific Domain Exception
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException ||
            capturedException instanceof InvalidTellerSessionStateException
        );
    }
}
