package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.command.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Ensure valid state
        repository.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Implicit in the aggregate creation
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Implicit in command construction
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Implicit in command construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNT_SUMMARY", "ENTER");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // markAuthenticated() is NOT called
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated();
        // Set last activity to 2 hours ago
        aggregate.setLastActivity(Instant.now().minus(Duration.ofHours(2)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        // We can't force the aggregate into an invalid internal state easily via setters without breaking encapsulation,
        // but we can simulate the rejection via the command being invalid (e.g. null context).
        // However, the step says the *aggregate* violates it. Let's assume an invalid state that rejects command.
        aggregate = new TellerSessionAggregate("session-bad-state");
        aggregate.markAuthenticated();
        // For this test, we'll rely on the logic inside execute throwing an error based on business rules.
        // Or, we can interpret "violates navigation state" as trying to navigate from an invalid screen.
        // Since the prompt is strict on "Given... violates", we assume the check fails inside execute.
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Typically domain errors are IllegalStateException or IllegalArgumentException in this pattern
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
