package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.MenuNavigatedEvent;
import com.example.domain.teller.model.NavigateMenuCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-001");
        aggregate.updateLastActivity(Instant.now());
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_auth() {
        aggregate = new TellerSessionAggregate("session-unauth");
        // Intentionally do not mark as authenticated
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-timeout");
        aggregate.markAuthenticated("teller-001");
        // Set last activity to 31 minutes ago (assuming 30 min timeout)
        aggregate.updateLastActivity(Instant.now().minus(Duration.ofMinutes(31)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-context");
        aggregate.markAuthenticated("teller-001");
        aggregate.updateLastActivity(Instant.now());
        // Do not initialize the navigation stack/menu context to simulate invalid state
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by using the aggregate created in Given steps
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Handled in the When step construction
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Handled in the When step construction
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            // Using generic IDs as constraints didn't specify specific values
            NavigateMenuCmd cmd = new NavigateMenuCmd("MENU_MAIN", "ENTER");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent);
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        assertEquals("MENU_MAIN", event.getMenuId());
        assertEquals("session-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException ||
                   capturedException instanceof UnknownCommandException);
    }
}
