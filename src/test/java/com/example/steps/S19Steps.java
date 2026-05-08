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

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        // Create a fresh, valid aggregate. We assume 'create' or init logic exists or we mock internal state.
        // For BDD, we simulate a pre-existing active session.
        aggregate = new TellerSessionAggregate("session-123");
        // Manually setting state to simulate an active, authenticated session for the positive flow
        // In a real app, this might be a SessionStartedCmd, but here we focus on Navigate.
        aggregate.forceStateForTest("teller-101", Instant.now(), true, "MAIN_MENU");
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate ID in Given
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in When block command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in When block command construction
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-violate-auth");
        // Ensure authenticated is false
        aggregate.forceStateForTest(null, Instant.now(), false, "LOGIN");
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-violate-timeout");
        // Set last activity to 2 hours ago
        aggregate.forceStateForTest("teller-101", Instant.now().minus(Duration.ofHours(2)), true, "MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-violate-context");
        // We simulate an invalid context (e.g. locked state or null current screen)
        aggregate.forceStateForTest("teller-101", Instant.now(), true, null);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // We construct the command dynamically. In a specific failure case, the input content matters less than the state.
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DEPOSIT_SCREEN", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("DEPOSIT_SCREEN", event.targetMenu());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected exception but command succeeded");
        // Checking for domain violation (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // Helper to expose the InMemoryRepository if we were using one, but here we instantiate Aggregate directly for speed/simplicity in BDD
}
