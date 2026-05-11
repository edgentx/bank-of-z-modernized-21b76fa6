package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.InvalidNavigationContextException;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.SessionInactiveException;
import com.example.domain.tellersession.model.SessionUnauthenticatedException;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a valid aggregate for the positive path
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String sessionId = "sess-123";
        aggregate = new TellerSessionAggregate(sessionId);
        // Simulate authentication logic (in real app, this would be an event)
        // For this unit test scenario, we assume the constructor or a test helper sets up the valid state
        // Here we manually set the internal state for the test context.
        // We'll assume the aggregate is created and initialized.
        // Since we need to test navigation, we assume it is authenticated and active.
        // We will reflect this in the aggregate's state via a hypothetical method or direct field access if it were a test package friend.
        // However, to keep encapsulation, we assume the aggregate starts in a valid state if no violation is specified.
        // To support the "violates" scenarios, we need to manipulate state.
        // Let's assume we can create it, then modify state via a test-specific setup method or commands.
        // For S-19, we will assume the TellerSessionAggregate has a method `initializeForTest()` to put it in a good state.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("sess-unauth");
        // Ensure it is NOT authenticated
        aggregate.markUnauthenticatedForTest();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("sess-timeout");
        aggregate.markAuthenticatedForTest();
        // Set last activity to 2 hours ago (assuming timeout is 15 mins)
        aggregate.forceLastActivity(Instant.now().minus(Duration.ofHours(2)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("sess-bad-context");
        aggregate.markAuthenticatedForTest();
        aggregate.markContextInvalidForTest();
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicit in the aggregate creation or command execution
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Implicit
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Implicit
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // For the positive case, ensure it is authenticated
            if (aggregate.id().equals("sess-123")) {
                 aggregate.markAuthenticatedForTest();
            }
            
            NavigateMenuCmd cmd = new NavigateMenuCmd("sess-123", "MainMenu", "Enter");
            // If the aggregate ID in the command doesn't match the aggregate instance,
            // the execute logic should handle it, but here we assume the aggregate handles the command.
            // We might need to adjust the command ID to match the specific aggregate instance ID for the test.
            
            String targetId = aggregate.id();
            cmd = new NavigateMenuCmd(targetId, "MainMenu", "Enter");
            
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MainMenu", event.menuId());
        assertEquals("Enter", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        // Check for specific exceptions based on the scenario
        if (aggregate.id().equals("sess-unauth")) {
            assertTrue(thrownException instanceof SessionUnauthenticatedException);
        } else if (aggregate.id().equals("sess-timeout")) {
            assertTrue(thrownException instanceof SessionInactiveException);
        } else if (aggregate.id().equals("sess-bad-context")) {
            assertTrue(thrownException instanceof InvalidNavigationContextException);
        }
    }
}
