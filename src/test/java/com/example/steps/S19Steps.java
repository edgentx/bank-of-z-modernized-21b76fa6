package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.InMemoryTellerSessionRepository;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Scenario 1 & 4: Valid / Context Check ---
    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Pre-auth for happy path
        aggregate.setLastActivity(Instant.now());
    }

    // --- Scenario 2: Authentication Violation ---
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally NOT marking authenticated
        aggregate.setLastActivity(Instant.now());
    }

    // --- Scenario 3: Timeout Violation ---
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Set last activity to 16 minutes ago (timeout is 15)
        aggregate.setLastActivity(Instant.now().minusSeconds(16 * 60));
    }

    // --- Scenario 4: Operational Context Violation ---
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        aggregate = new TellerSessionAggregate("session-context");
        aggregate.markAuthenticated();
        aggregate.setLastActivity(Instant.now());
        // The violation will be triggered by passing nulls/invalids in the Command
    }

    // --- Common Givens ---
    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled by aggregate initialization
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Stored in context, will be used in Command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Stored in context, will be used in Command construction
    }

    // --- When ---
    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // Scenario 4 path: Passing empty strings to trigger context error
            String menu = aggregate.getCurrentMenuId() != null ? "MainMenu" : "";
            String action = "ENTER";

            // If we are testing the context violation, we pass bad data
            if (aggregate.id().equals("session-context")) {
                menu = null; // Trigger context violation
            }

            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), menu, action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Then ---
    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);

        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("session-123", event.aggregateId());
        assertEquals("MainMenu", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
