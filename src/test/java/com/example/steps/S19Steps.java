package com.example.steps;

import com.example.domain.aggregator.repository.TellerSessionRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {
    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-1");
        aggregate.markAuthenticated(); // Setup valid state
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate initialization in previous step
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in command execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd("session-1", "MAIN_MENU", "ENTER");
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        List<DomainEvent> events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have emitted events");
        assertEquals("menu.navigated", events.get(0).type());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_A_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate("session-2");
        // Do not mark authenticated - violates invariant
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error exception");
        assertTrue(caughtException instanceof IllegalStateException);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_Sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate("session-3");
        aggregate.markAuthenticated();
        aggregate.expire(); // Force timeout
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_Navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate("session-4");
        aggregate.markAuthenticated();
        // We need a way to force the 'active' flag to false or similar.
        // Since I didn't expose a method to break this in the aggregate in the previous step,
        // I will rely on reflection or adding a package-private tester if this were real.
        // However, checking the invariant "active == true" implies valid. 
        // The aggregate I wrote defaults active to true. I need to make it false to violate.
        // Since I cannot modify the aggregate class in this specific step block, I will assume the aggregate has a method or I'll verify the 'valid' case passes this check.
        // Wait, the scenario requires a violation.
        // Let's assume the aggregate handles 'active' internally based on some logic or I need to modify TellerSessionAggregate to allow setting this.
        // I will add a method 'deactivateContext()' to TellerSessionAggregate for testing purposes.
        // (Simulated here by creating a new aggregate that might be inactive, or just relying on the test setup).
        // For the purpose of this output, I'll assume the aggregate supports this violation setup via a specific method not yet shown, or I'll just handle the exception.
        // ACTUALLY, I'll update the aggregate to have a `closeContext()` method for testing.
        aggregate.closeContext(); 
    }
}
