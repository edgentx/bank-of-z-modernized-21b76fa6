package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.SessionNavigatedEvent;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.tellersession.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        // Create a valid, authenticated, active session
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Bypass command execution for setup to ensure clean state for the specific test
        aggregate.hydrate(
            "teller-123", 
            true, 
            Instant.now().minusSeconds(60), 
            "MAIN_MENU", 
            Instant.now().plusSeconds(300)
        );
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Handled by aggregate initialization in previous step
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        // Handled in command execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in command execution
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.hydrate(
            "teller-123", 
            false, // Not authenticated
            Instant.now().minusSeconds(60), 
            "MAIN_MENU", 
            Instant.now().plusSeconds(300)
        );
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        // Last active 20 minutes ago, timeout is 15 minutes
        Instant lastActive = Instant.now().minus(Duration.ofMinutes(20));
        aggregate.hydrate(
            "teller-123", 
            true, 
            lastActive, 
            "MAIN_MENU", 
            Instant.now().plusSeconds(300)
        );
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_nav_state() {
        String sessionId = UUID.randomUUID().toString();
        this.aggregate = new TellerSessionAggregate(sessionId);
        aggregate.hydrate(
            "teller-123", 
            true, 
            Instant.now().minusSeconds(60), 
            "UNKNOWN_CONTEXT", // Invalid state
            Instant.now().plusSeconds(300)
        );
        repository.save(aggregate);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            String targetMenuId = "ACCOUNT_DETAILS";
            String action = "ENTER";
            Command cmd = new NavigateMenuCmd(aggregate.id(), targetMenuId, action);
            
            // Load fresh aggregate to simulate persistence load
            TellerSessionAggregate loaded = repository.findById(aggregate.id()).orElseThrow();
            this.resultEvents = loaded.execute(cmd);
            
            // Save updated state (in memory)
            repository.save(loaded);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionNavigatedEvent);
        
        SessionNavigatedEvent event = (SessionNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals("ACCOUNT_DETAILS", event.targetMenuId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // Ideally a specific DomainException, but RuntimeException/IllegalStateException fits the prompt's shared contracts
        Assertions.assertTrue(capturedException instanceof IllegalStateException || 
                              capturedException instanceof IllegalArgumentException);
    }
}
