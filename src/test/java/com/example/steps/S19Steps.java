package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Create a valid session ID
        aggregate = new TellerSessionAggregate("session-123");
        // Prime the aggregate to a valid, authenticated, active state using system command/event
        // Assuming TellerSessionInitiatedCmd exists to bootstrap state for testing purposes
        // For this exercise, we simulate the 'valid' state by applying logic or pre-setting state flags
        // via reflection or a factory method if available. Here we assume a helper to hydrate valid state.
        
        // Hydrating the aggregate directly (Testing backdoor)
        // In a real scenario, we would issue an InitiateSessionCmd.
        // This simulates the post-initiation state.
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivity(java.time.Instant.now());
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate creation
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context loaded in 'When' step via command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context loaded in 'When' step via command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-auth-fail");
        aggregate.setAuthenticated(false); // Not authenticated
        aggregate.setActive(true);
        aggregate.setLastActivity(java.time.Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        // Set last activity to 2 hours ago (Configured timeout usually 15 mins)
        aggregate.setLastActivity(java.time.Instant.now().minusSeconds(7200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        aggregate = new TellerSessionAggregate("session-nav-fail");
        aggregate.setAuthenticated(true);
        aggregate.setActive(true);
        aggregate.setLastActivity(java.time.Instant.now());
        // Simulate a state mismatch or lock
        aggregate.setLocked(true);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        caughtException = null;
        try {
            // Construct valid command inputs
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected exception was not thrown");
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException,
            "Exception should be a domain error (IllegalArgument or IllegalState)"
        );
    }
}
