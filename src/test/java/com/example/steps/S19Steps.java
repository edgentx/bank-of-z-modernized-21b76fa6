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
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    // Using In-Memory Aggregate directly as per DDD Unit Test pattern
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Helper to create a valid aggregate ---
    private TellerSessionAggregate createValidSession(String sessionId) {
        TellerSessionAggregate agg = new TellerSessionAggregate(sessionId);
        // To create a 'valid' session in a state ready for navigation, we assume
        // a previous event occurred (e.g. SessionInitiated). We will simulate state hydration manually
        // or assume the aggregate is initialized with valid defaults for this specific command test.
        // For this test, we will initialize fields directly to represent a hydrated, authenticated state.
        agg.setAuthenticated(true);
        agg.setLastActivity(Instant.now());
        agg.setCurrentMenu("MAIN_MENU"); // Valid operational context
        return agg;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        this.aggregate = createValidSession("SESSION-101");
        this.capturedException = null;
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.aggregate = new TellerSessionAggregate("SESSION-102");
        this.aggregate.setAuthenticated(false);
        this.aggregate.setLastActivity(Instant.now());
        this.aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.aggregate = new TellerSessionAggregate("SESSION-103");
        this.aggregate.setAuthenticated(true);
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        this.aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
        this.aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        this.aggregate = new TellerSessionAggregate("SESSION-104");
        this.aggregate.setAuthenticated(true);
        this.aggregate.setLastActivity(Instant.now());
        // Set current menu to null or invalid state
        this.aggregate.setCurrentMenu(null);
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate constructor in Given steps
        // In real scenario, this would be part of the command object construction
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in 'When'
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in 'When'
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        String sessionId = aggregate.id();
        String targetMenu = "ACCOUNT_DETAILS";
        String action = "ENTER";

        NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, targetMenu, action);

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Checking for IllegalStateException, IllegalArgumentException, or UnknownCommandException
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException ||
            capturedException instanceof UnknownCommandException,
            "Expected a domain-specific exception (IllegalStateException/IllegalArgumentException), but got: " + capturedException.getClass().getSimpleName()
        );
    }
}
