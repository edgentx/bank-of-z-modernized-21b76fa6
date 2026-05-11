package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    static class InMemoryTellerSessionRepository implements TellerSessionRepository {
        private TellerSessionAggregate store;
        @Override
        public void save(TellerSessionAggregate aggregate) {
            this.store = aggregate;
        }
        @Override
        public Optional<TellerSessionAggregate> findById(String id) {
            return Optional.ofNullable(store);
        }
    }

    @Given("a valid TellerSession aggregate")
    public void aValidTellerSessionAggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated(); // Setup authenticated state
        aggregate.setCurrentMenu("MAIN_MENU");
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void aTellerSessionAggregateThatViolatesAuthentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // isAuthenticated defaults to false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void aTellerSessionAggregateThatViolatesTimeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated();
        aggregate.markExpired(); // Set last activity to 16 minutes ago
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void aTellerSessionAggregateThatViolatesNavigationState() {
        aggregate = new TellerSessionAggregate("session-400");
        aggregate.markAuthenticated();
        aggregate.setCurrentMenu(null); // No current menu, trying to go 'back'
    }

    @Given("a valid sessionId is provided")
    public void aValidSessionIdIsProvided() {
        // Handled implicitly by aggregate creation in previous steps
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void aValidMenuIdIsProvided() {
        // Handled in the 'When' step construction
    }

    @Given("a valid action is provided")
    public void aValidActionIsProvided() {
        // Handled in the 'When' step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void theNavigateMenuCmdCommandIsExecuted() {
        try {
            // For success scenario, we navigate forward.
            // For error scenarios, we might pick an invalid action like 'back' when no current menu exists.
            String action = "enter";
            if (aggregate.getCurrentMenu() == null) {
                // Force the context error condition for the 4th scenario
                action = "back"; 
            }
            
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "DEPOSIT_SCREEN", action);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void aMenuNavigatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("menu.navigated", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("DEPOSIT_SCREEN", event.menuId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // We check it's an IllegalStateException (domain invariant violation)
        assertTrue(capturedException instanceof IllegalStateException);
        System.out.println("Caught expected domain error: " + capturedException.getMessage());
    }
    
    // Helper access for test visibility
    public String getCurrentMenu() {
        // In a real app we might expose this via a getter, here we assume introspection or public getter in Aggregate
        // The aggregate doesn't explicitly expose a getter for currentMenuId in the prompt, so we rely on the event logic
        // or we could add a getter to TellerSessionAggregate.
        return aggregate != null ? aggregate.currentMenuId : null; // Assumes package-private or public access, or we add getter.
    }
}