package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate session;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = "session-123";
        // Create a valid, authenticated, non-timed out session
        this.session = new TellerSessionAggregate(sessionId);
        
        // Simulate authentication via login logic or internal state manipulation
        // Ideally we would execute a LoginCmd, but for isolation we set the internal state
        // In a real repo, we might have a factory method.
        // Here we assume a standard constructor + internal state setup for test.
        // However, the aggregate constructor likely sets defaults.
        // We will assume we can call execute with a Login command if it existed, 
        // but here we can use a package-private or reflection setup if needed. 
        // To keep it clean, let's assume the session starts valid or we can execute a cmd.
        
        // Simulating Login Event Application
        session.applyLogin(new TellerLoggedInEvent(sessionId, "teller-1", Instant.now()));
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        this.session = new TellerSessionAggregate("session-unauth");
        // Do not call applyLogin. isAuthenticated defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        this.session = new TellerSessionAggregate("session-timeout");
        session.applyLogin(new TellerLoggedInEvent("session-timeout", "teller-1", Instant.now().minus(Duration.ofHours(2))));
        // Assuming session timeout is < 2 hours (e.g., 15 mins)
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        this.session = new TellerSessionAggregate("session-context");
        session.applyLogin(new TellerLoggedInEvent("session-context", "teller-1", Instant.now()));
        // We need a state where the navigation is invalid. 
        // e.g., trying to perform an action meant for 'MENU_A' while currently in 'MENU_B'
        // or the screen is locked/busy.
        session.setContext("LOCKED_SCREEN"); // Simulate a bad state
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled in aggregate construction
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Will be passed in the command
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Will be passed in the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Create command
            // Using a standard menuId/action that should succeed if state is valid
            NavigateMenuCmd cmd = new NavigateMenuCmd("MAIN_MENU", "ENTER");
            resultingEvents = session.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultingEvents, "Events list should not be null");
        Assertions.assertFalse(resultingEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals("menu.navigated", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // We check for specific exception types (IllegalStateException, IllegalArgumentException) or UnknownCommandException
        // based on the specific scenario logic implemented in the aggregate.
        // The generic check is that it's a RuntimeException.
        Assertions.assertTrue(capturedException instanceof RuntimeException);
    }
}
