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

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Cucumber Steps for S-19: NavigateMenuCmd.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private NavigateMenuCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception caughtException;

    // Constants matching Aggregate defaults
    private static final Duration TIMEOUT_DURATION = Duration.ofMinutes(15);

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.aggregate = new TellerSessionAggregate("session-123");
        // Ensure valid state (authenticated)
        this.aggregate.markAuthenticated(); 
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the aggregate construction, or valid command construction
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Construct a valid command
            cmd = new NavigateMenuCmd("session-123", "MAIN_MENU", "ENTER");
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals("session-123", event.aggregateId());
        Assertions.assertEquals("MAIN_MENU", event.menuId());
    }

    // ---------------------------------------------------------------
    // Negative Scenarios
    // ---------------------------------------------------------------

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate("session-auth-fail");
        // Do NOT call markAuthenticated(). Defaults to false.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate("session-timeout-fail");
        this.aggregate.markAuthenticated(); // Make it valid otherwise
        // Set last activity to 16 minutes ago (Configured timeout is 15m)
        this.aggregate.setLastActivityAt(Instant.now().minus(Duration.ofMinutes(16)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        this.aggregate = new TellerSessionAggregate("session-context-fail");
        this.aggregate.markAuthenticated(); // Make it valid otherwise
        this.aggregate.lockNavigation(); // Simulate a context error/lock
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception, but command succeeded.");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, 
            "Expected IllegalStateException, got: " + caughtException.getClass().getSimpleName());
    }
}
