package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        this.sessionId = "sess-123";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Initialize to a valid authenticated state via reflection or direct initialization if supported.
        // Since TellerSessionAggregate is new in this story, we assume it allows initialization or has a factory.
        // Here we simulate the aggregate being in a valid, authenticated state by bypassing the 'login' command for simplicity of unit testing navigation logic,
        // or we would execute a hypothetical LoginCmd first.
        // For the purpose of this test, we assume the constructor creates a valid skeleton, but we must ensure invariants pass.
        // We will use reflection to set the internal state to 'authenticated' and 'active' to test navigation success.
        try {
            var fieldAuthenticated = TellerSessionAggregate.class.getDeclaredField("authenticated");
            fieldAuthenticated.setAccessible(true);
            fieldAuthenticated.setBoolean(aggregate, true);

            var fieldLastActivity = TellerSessionAggregate.class.getDeclaredField("lastActivityAt");
            fieldLastActivity.setAccessible(true);
            fieldLastActivity.set(aggregate, Instant.now());
        } catch (Exception e) {
            fail("Failed to setup test aggregate state: " + e.getMessage());
        }
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        this.sessionId = "sess-123";
        // Used in command construction
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        this.menuId = "MAIN_MENU";
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(sessionId, event.aggregateId());
        assertEquals(menuId, event.menuId());
        assertEquals(action, event.action());
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.sessionId = "sess-unauth";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Ensure authenticated is false (default)
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.sessionId = "sess-timeout";
        this.aggregate = new TellerSessionAggregate(sessionId);
        try {
            var field = TellerSessionAggregate.class.getDeclaredField("lastActivityAt");
            field.setAccessible(true);
            // Set activity to 30 minutes ago (assuming timeout is 15 mins)
            field.set(aggregate, Instant.now().minus(Duration.ofMinutes(30)));
        } catch (Exception e) {
            fail("Failed to setup test aggregate state: " + e.getMessage());
        }
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        this.sessionId = "sess-bad-state";
        this.aggregate = new TellerSessionAggregate(sessionId);
        // We assume this scenario handles an invalid menuId or action in the context
        // The aggregate will verify this. Let's pick an invalid action context.
        this.action = "INVALID_ACTION_FOR_STATE";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Check if it's an IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
