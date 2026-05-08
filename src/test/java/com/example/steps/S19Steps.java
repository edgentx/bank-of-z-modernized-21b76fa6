package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        String id = UUID.randomUUID().toString();
        // Simulate an authenticated, active session
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivity(Instant.now());
        aggregate.setCurrentScreen("MAIN_MENU");
        aggregate.setTimeout(Duration.ofMinutes(15));
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Session ID is implicitly handled by loading the aggregate created above
        Assertions.assertNotNull(aggregate);
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Input for the command is handled in the 'When' step
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Input for the command is handled in the 'When' step
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuth() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(false);
        aggregate.setLastActivity(Instant.now());
        aggregate.setTimeout(Duration.ofMinutes(15));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(true);
        // Simulate a session last active 20 minutes ago, with a 15 minute timeout
        aggregate.setLastActivity(Instant.now().minus(Duration.ofMinutes(20)));
        aggregate.setTimeout(Duration.ofMinutes(15));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavState() {
        String id = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(id);
        aggregate.setAuthenticated(true);
        aggregate.setLastActivity(Instant.now());
        aggregate.setTimeout(Duration.ofMinutes(15));
        // Set current screen to a state that might prevent navigation (e.g., locked or transaction in progress)
        // For this BDD, we assume being in 'LOCKED' state prevents standard navigation
        aggregate.setCurrentScreen("LOCKED");
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            String targetMenu = "ACCOUNT_DETAILS";
            String action = "ENTER";
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), targetMenu, action);
            
            // Reload from repository to ensure fresh state
            TellerSessionAggregate agg = repository.load(aggregate.id());
            this.resultEvents = agg.execute(cmd);
            
            // Save back to reflect state changes if any (though in-mem is fine for test scope)
            repository.save(agg);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        Assertions.assertEquals(MenuNavigatedEvent.class, resultEvents.get(0).getClass());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // We expect IllegalStateException or IllegalArgumentException for domain rule violations
        Assertions.assertTrue(IllegalStateException.class.isAssignableFrom(capturedException.getClass()) ||
                              IllegalArgumentException.class.isAssignableFrom(capturedException.getClass()));
    }

    // Inner class for in-memory repository
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final java.util.Map<String, TellerSessionAggregate> store = new java.util.HashMap<>();

        @Override
        public TellerSessionAggregate load(String id) {
            TellerSessionAggregate agg = store.get(id);
            if (agg == null) throw new RuntimeException("Aggregate not found: " + id);
            return agg;
        }

        @Override
        public void save(TellerSessionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }
    }
}
