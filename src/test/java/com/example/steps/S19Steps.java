package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-19: TellerSession Navigation.
 */
public class S19Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        aggregate = new TellerSessionAggregate("session-123");
        aggregate.markAuthenticated("teller-01");
        aggregate.clearEvents(); // Clear creation events if any
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("session-401");
        // Intentionally do not mark as authenticated
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("session-408");
        aggregate.markAuthenticated("teller-01");
        aggregate.markTimedOut();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_context() {
        aggregate = new TellerSessionAggregate("session-400");
        aggregate.markAuthenticated("teller-01");
        aggregate.markContextInvalid("BLOCKED");
    }

    @And("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // Handled by aggregate construction in Given steps
        assertNotNull(aggregate.id());
    }

    @And("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Placeholder for verification, mostly handled in 'When'
    }

    @And("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Placeholder for verification
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        var cmd = new NavigateMenuCmd(aggregate.id(), "ACCOUNT_SUMMARY", "ENTER");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We assert it is either IllegalStateException or IllegalArgumentException as per domain rules
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
