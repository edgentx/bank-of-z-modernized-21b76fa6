package com.example.steps;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterfacenavigation.model.MenuNavigatedEvent;
import com.example.domain.userinterfacenavigation.model.NavigateMenuCmd;
import com.example.domain.userinterfacenavigation.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Default valid state: authenticated, active now, on Main Menu
        this.aggregate = new TellerSessionAggregate(
            "session-123",
            true,
            Instant.now(),
            "MAIN"
        );
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by the aggregate initialization in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the 'When' step via command construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the 'When' step via command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        NavigateMenuCmd cmd = new NavigateMenuCmd("session-123", "ACCOUNTS", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("ACCOUNTS", event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        this.aggregate = new TellerSessionAggregate(
            "session-bad-auth",
            false, // Not authenticated
            Instant.now(),
            "MAIN"
        );
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        this.aggregate = new TellerSessionAggregate(
            "session-timed-out",
            true,
            Instant.now().minus(20, ChronoUnit.MINUTES), // 20 mins ago > 15 min timeout
            "MAIN"
        );
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        this.aggregate = new TellerSessionAggregate(
            "session-bad-nav",
            true,
            Instant.now(),
            "MAIN" // Trying to go BACK from MAIN is invalid
        );
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // Override the When for negative scenarios to check specific failure logic if needed,
    // but the generic "the NavigateMenuCmd command is executed" works for both.
    // We just need to ensure the command triggers the specific violation.
    
    // Context-specific When hooks could be used if the command payload differed per scenario,
    // but here the violation depends on the *Aggregate State*, not the command payload.
}
