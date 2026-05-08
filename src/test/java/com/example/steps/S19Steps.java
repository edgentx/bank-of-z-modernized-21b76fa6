package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Valid test data defaults
    private static final String VALID_SESSION_ID = "SESSION-101";
    private static final String VALID_TELLER_ID = "TELLER-01";
    private static final String VALID_MENU_ID = "MAIN_MENU";
    private static final String VALID_ACTION = "ENTER";

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Hydrate with valid authenticated state
        aggregate.hydrate(VALID_TELLER_ID, true, Instant.now(), "HOME_SCREEN", false);
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled in the command creation steps
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the command creation steps
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the command creation steps
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Hydrate with unauthenticated state
        aggregate.hydrate(VALID_TELLER_ID, false, Instant.now(), "LOGIN_SCREEN", false);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Hydrate with authenticated but stale timestamp
        // Default timeout is 15 minutes. Let's go back 16 minutes.
        Instant staleTime = Instant.now().minus(Duration.ofMinutes(16));
        aggregate.hydrate(VALID_TELLER_ID, true, staleTime, "HOME_SCREEN", false);
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate(VALID_SESSION_ID);
        // Hydrate with authenticated, active, but LOCKED state
        aggregate.hydrate(VALID_TELLER_ID, true, Instant.now(), "TRANSACTION_IN_PROGRESS", true);
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        // Reload from repository to ensure persistence consistency
        Optional<TellerSessionAggregate> loaded = repository.findById(VALID_SESSION_ID);
        if (loaded.isEmpty()) {
            fail("Aggregate not found in repository");
        }
        
        aggregate = loaded.get();
        NavigateMenuCmd cmd = new NavigateMenuCmd(VALID_SESSION_ID, VALID_TELLER_ID, VALID_MENU_ID, VALID_ACTION);
        
        try {
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate); // Save changes if successful
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertFalse(resultEvents.isEmpty(), "Expected at least one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof MenuNavigatedEvent, "Expected MenuNavigatedEvent");
        
        MenuNavigatedEvent navEvent = (MenuNavigatedEvent) event;
        assertEquals("menu.navigated", navEvent.type());
        assertEquals(VALID_MENU_ID, navEvent.targetMenuId());
        assertEquals(VALID_ACTION, navEvent.action());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        
        // We expect IllegalStateException or IllegalArgumentException for domain rejections
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected domain error exception, got: " + capturedException.getClass().getSimpleName()
        );
        
        // Verify the message contains relevant context
        assertTrue(capturedException.getMessage().length() > 0);
    }

    // --- In-Memory Repository Implementation ---
    private static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private final java.util.Map<String, TellerSessionAggregate> store = new java.util.HashMap<>();

        @Override
        public void save(TellerSessionAggregate aggregate) {
            store.put(aggregate.id(), aggregate);
        }

        @Override
        public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }
    }
}
