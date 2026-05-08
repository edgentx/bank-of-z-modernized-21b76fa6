package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = "session-1";
        aggregate = new TellerSessionAggregate(id);
        // Bootstrap to a valid state (Authenticated, Active)
        aggregate.apply(new TellerSessionAuthenticatedEvent(id, "teller-1", java.time.Instant.now()));
        aggregate.apply(new TellerSessionActivatedEvent(id, "MAIN_MENU", java.time.Instant.now()));
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by the aggregate creation in the previous step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled implicitly, we will use it in the command
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled implicitly, we will use it in the command
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd("session-1", "DEPOSIT_MENU", "ENTER");
            resultingEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Persist state changes in memory
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        assertEquals("DEPOSIT_MENU", event.menuId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-2");
        // Created but NOT authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = "session-3";
        aggregate = new TellerSessionAggregate(id);
        aggregate.apply(new TellerSessionAuthenticatedEvent(id, "teller-1", java.time.Instant.now().minusSeconds(3600))); // 1 hour ago
        aggregate.apply(new TellerSessionActivatedEvent(id, "MAIN_MENU", java.time.Instant.now().minusSeconds(3600)));
        // Note: The aggregate logic will check the last activity time against a timeout threshold.
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String id = "session-4";
        aggregate = new TellerSessionAggregate(id);
        aggregate.apply(new TellerSessionAuthenticatedEvent(id, "teller-1", java.time.Instant.now()));
        aggregate.apply(new TellerSessionActivatedEvent(id, "MAIN_MENU", java.time.Instant.now()));
        // We will attempt to navigate to a menu that doesn't exist or is invalid in current context
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    // --- Wiring for specific command execution in negative flows ---
    // (We reuse the When step, but context is set up by the Given steps above)
}
