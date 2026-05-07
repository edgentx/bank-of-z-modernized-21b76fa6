package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure authenticated for success case
        aggregate.setLastActivityAt(Instant.now()); // Ensure active
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate initialization in the previous step
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Context: Handled in the 'When' step construction of the command
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Context: Handled in the 'When' step construction of the command
    }

    // --- Violations (Negative Givens) ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth-123");
        // Do NOT call markAuthenticated()
        aggregate.setLastActivityAt(Instant.now());
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout-123");
        aggregate.markAuthenticated();
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minusSeconds(1200));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationContext() {
        aggregate = new TellerSessionAggregate("session-context-123");
        aggregate.markAuthenticated();
        aggregate.setLastActivityAt(Instant.now());
        // The violation in this scenario is triggered by sending invalid command data (null/blank)
        // which the aggregate interprets as a context violation.
        // We handle this in the 'When' step logic conditionally or via a shared flag.
        // For this implementation, we'll use a specific command in the When step.
    }

    // --- Whens ---

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        Command cmd;

        // Check if we are in the "Navigation Context Violation" scenario
        // We identify this by checking the aggregate ID or a flag, but here we check the ID
        // for simplicity based on the Given steps above.
        if (aggregate.id().equals("session-context-123")) {
            // Send invalid context (blank menuId)
            cmd = new NavigateMenuCmd(aggregate.id(), "", "enter");
        } else {
            // Send valid context
            cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "enter");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Thens ---

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals("MAIN_MENU", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // Usually, domain errors are IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}