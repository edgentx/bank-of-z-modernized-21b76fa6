package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermenu.model.*;
import com.example.domain.tellermenu.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionRepository repo = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = "session-123";
        aggregate = new TellerSessionAggregate(id);
        // Initialize state as if logged in to pass authentication invariant
        aggregate.applyHistory(new TellerSessionAuthenticatedEvent(id, "teller-1", Instant.now()));
        repo.save(aggregate);
    }

    @And("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Using the ID from the valid aggregate creation
        Assertions.assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Data is contained within the command construction in 'When'
    }

    @And("a valid action is provided")
    public void aValidActionIsProvided() {
        // Data is contained within the command construction in 'When'
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU", "ENTER");
            List<DomainEvent> events = aggregate.execute(cmd);
            // Apply events locally for this test scenario to update state if needed
            events.forEach(e -> {
                if (e instanceof MenuNavigatedEvent) {
                    // In a real app, we might apply this to the aggregate state, 
                    // but the command handler in the aggregate usually does this.
                    // Since we are testing the execute method which returns events,
                    // we verify the return value in the 'Then' step.
                }
            });
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        Assertions.assertTrue(aggregate.uncommittedEvents().get(0) instanceof MenuNavigatedEvent);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        String id = "session-unauth";
        aggregate = new TellerSessionAggregate(id);
        // Do NOT apply the authenticated event. State remains null/empty.
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = "session-timeout";
        aggregate = new TellerSessionAggregate(id);
        // Authenticate, but set the last activity time to far in the past
        Instant past = Instant.now().minus(Duration.ofHours(2));
        aggregate.applyHistory(new TellerSessionAuthenticatedEvent(id, "teller-1", past));
        repo.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesContext() {
        String id = "session-bad-ctx";
        aggregate = new TellerSessionAggregate(id);
        aggregate.applyHistory(new TellerSessionAuthenticatedEvent(id, "teller-1", Instant.now()));
        // Simulate being in a state where navigation is locked or invalid (e.g. Locked state)
        aggregate.lockSessionForTest(); 
        repo.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Depending on implementation, this might be IllegalStateException or a custom DomainException
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}