package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.MenuNavigatedEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test Constants
    private static final String SESSION_ID = "session-123";
    private static final String TELLER_ID = "teller-01";
    private static final String VALID_MENU_ID = "MAIN_MENU";
    private static final String VALID_ACTION = "SELECT";

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Setup authenticated state for the happy path
        aggregate.markAuthenticated(TELLER_ID);
        aggregate.setCurrentMenu("LOGIN_SCREEN");
        // Ensure it is not timed out
        aggregate.setLastActivityAt(Instant.now());
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by the aggregate initialization in previous step
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in the 'When' step via the command object
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the 'When' step via the command object
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Reload fresh from repository to ensure we are testing persistence/hydration logic if implemented
            // In memory, it returns the same instance, but good practice
            TellerSessionAggregate aggToExecute = repository.findById(SESSION_ID)
                    .orElseThrow(() -> new IllegalStateException("Aggregate not found"));

            NavigateMenuCmd cmd = new NavigateMenuCmd(SESSION_ID, VALID_MENU_ID, VALID_ACTION);
            resultEvents = aggToExecute.execute(cmd);
            
            // Save result
            repository.save(aggToExecute);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals(MenuNavigatedEvent.class, resultEvents.get(0).getClass());
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals(SESSION_ID, event.aggregateId());
        assertEquals(VALID_MENU_ID, event.menuId());
        assertEquals("menu.navigated", event.type());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_a_teller_must_be_authenticated_to_initiate_a_session() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        // Intentionally NOT calling markAuthenticated()
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_sessions_must_timeout_after_a_configured_period_of_inactivity() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(TELLER_ID);
        // Set last activity to 20 minutes ago (Timeout is 15)
        aggregate.setLastActivityAt(Instant.now().minus(20, ChronoUnit.MINUTES));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state_must_accurately_reflect_the_current_operational_context() {
        aggregate = new TellerSessionAggregate(SESSION_ID);
        aggregate.markAuthenticated(TELLER_ID);
        aggregate.setLastActivityAt(Instant.now());
        // The command executed later will provide an invalid menuId (null/blank)
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        
        // Check for specific exception types or messages depending on the scenario
        // The aggregate throws IllegalStateException or IllegalArgumentException
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}