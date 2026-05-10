package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellermenu.model.*;
import com.example.domain.tellermenu.repository.TellerSessionRepository;
import com.example.exceptions.*;
import com.example.mocks.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S19Steps {

    private TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        // Defaults: authenticated, active, valid state
        aggregate = new TellerSessionAggregate("session-123", "teller-01");
        aggregate.markAuthenticated();
        aggregate.markOperational();
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_not_authenticated() {
        // State: NOT authenticated
        aggregate = new TellerSessionAggregate("session-999", "teller-01");
        // Intentionally do not call markAuthenticated()
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_expired() {
        aggregate = new TellerSessionAggregate("session-888", "teller-01");
        aggregate.markAuthenticated();
        // Simulate timeout by setting last activity to distant past
        aggregate.forceLastActivityTime(Instant.now().minus(Duration.ofMinutes(31)));
        repository.save(aggregate);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_invalid_context() {
        aggregate = new TellerSessionAggregate("session-777", "teller-01");
        aggregate.markAuthenticated();
        // Intentionally do not mark operational (or mark locked/suspended)
        // causing context mismatch for navigation
        repository.save(aggregate);
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled implicitly by the aggregate creation in previous steps
        // We verify it exists in repo
        Assertions.assertNotNull(repository.findById(aggregate.id()));
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Placeholder for specific data setup if needed
        // The command payload will carry this data
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Placeholder for specific data setup if needed
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            Command cmd = new NavigateMenuCmd(aggregate.id(), "MAIN_MENU_01", "ENTER");
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalStateException | DomainException e) {
            caughtException = e;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents, "Events should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should have emitted one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof MenuNavigatedEvent, "Event should be MenuNavigatedEvent");
        
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultEvents.get(0);
        Assertions.assertEquals("menu.navigated", event.type());
        Assertions.assertEquals(aggregate.id(), event.aggregateId());
        Assertions.assertEquals("MAIN_MENU_01", event.targetMenuId());
        Assertions.assertEquals("ENTER", event.action());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // We accept IllegalStateException or custom DomainExceptions as Domain Errors here
        // Ideally specific custom exceptions, but State mismatches often yield IllegalStateException in this pattern
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof DomainException,
            "Expected a domain error (IllegalStateException or DomainException), got: " + caughtException.getClass().getSimpleName()
        );
    }

    // JUnit 5 Test Suite configuration for Cucumber
    // This would typically go in a separate S19TestSuite.java, but including here for completeness if inline required
    /* 
    @Suite
    @SelectClasspathResource("features")
    @IncludeEngines("cucumber")
    public class S19TestSuite {}
    */
}
