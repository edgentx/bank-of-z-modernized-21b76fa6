package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class S19Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = UUID.randomUUID().toString();
        // Assume a constructor or factory that creates an authenticated, active session
        // For the purpose of the 'Valid' scenario, we assume the internal state is set to valid defaults
        // In a real repo, this might be: aggregate = new TellerSessionAggregate(sessionId, true, Instant.now().plusSeconds(30), true);
        this.aggregate = new TellerSessionAggregate(sessionId); 
        // We rely on the aggregate having a protected state or constructor that allows setting up the 'Valid' state
        // Since we can't change the signature of the stub constructor easily without editing the file, 
        // we will assume the execute logic handles the state checks internally. 
        // For the negative tests, we need a way to set state. We will assume the Aggregate allows this or we mock the behavior.
        // *However*, since we control the code generation, we will add a factory method to the Aggregate in the domain phase.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID set in previous step
        Assertions.assertNotNull(sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        this.menuId = "MAIN_MENU_01";
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        this.action = "ENTER";
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        Command cmd = new NavigateMenuCmd(sessionId, menuId, action);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertEquals("teller.session.menu.navigated", resultingEvents.get(0).type());
        Assertions.assertEquals(sessionId, resultingEvents.get(0).aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = UUID.randomUUID().toString();
        this.menuId = "MENU_01";
        this.action = "ENTER";
        // We need an aggregate instance that is NOT authenticated. 
        // We will use a static factory or specific constructor we add to the domain class.
        this.aggregate = TellerSessionAggregate.createUnauthenticated(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = UUID.randomUUID().toString();
        this.menuId = "MENU_01";
        this.action = "ENTER";
        // Create an aggregate that is timed out
        this.aggregate = TellerSessionAggregate.createTimedOut(sessionId);
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        this.sessionId = UUID.randomUUID().toString();
        this.menuId = "MENU_01";
        this.action = "ENTER";
        // Create an aggregate that is in an invalid context (e.g. locked)
        this.aggregate = TellerSessionAggregate.createInvalidContext(sessionId);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // Verify it's a domain exception (IllegalStateException or IllegalArgumentException usually)
        Assertions.assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
        Assertions.assertNull(resultingEvents);
    }
}
