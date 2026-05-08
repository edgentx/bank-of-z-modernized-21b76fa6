package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.tellersession.model.NavigateMenuCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S19Steps {

    private Aggregate aggregate;
    private String sessionId;
    private String menuId;
    private String action;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        this.sessionId = "sess-123";
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
        
        // We simulate a fully hydrated aggregate by creating one and applying a "created" event conceptually 
        // (or just trusting the aggregate constructor sets up valid defaults).
        this.aggregate = new TellerSessionAggregate(this.sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        this.sessionId = "sess-unauth";
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
        
        // Create aggregate in a state where isAuthenticated is false (default for new uninitialized)
        this.aggregate = new TellerSessionAggregate(this.sessionId);
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        this.sessionId = "sess-timeout";
        this.menuId = "MAIN_MENU";
        this.action = "ENTER";
        
        // Create aggregate and force it into a timed out state using a test-specific hook or constructor
        // Assuming a constructor that allows setting lastActivityTime for testing scenarios
        this.aggregate = new TellerSessionAggregate(this.sessionId, Instant.now().minus(Duration.ofMinutes(31)));
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_context() {
        this.sessionId = "sess-bad-context";
        this.menuId = "INVALID_MENU_FOR_CONTEXT";
        this.action = "ENTER";
        
        // Create aggregate in a specific operational context (e.g. In Transaction) that forbids the target menu
        this.aggregate = new TellerSessionAggregate(this.sessionId);
        // Hypothetical method to set internal state for testing the invariant check
        // ((TellerSessionAggregate) aggregate).forceContext(Context.IN_TRANSACTION); 
        // For this test, the aggregate implementation will handle the failure logic based on internal state.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // Session ID is set in the 'Given' steps above
        assertNotNull(this.sessionId);
    }

    @Given("a valid menuId is provided")
    public void a_valid_menu_id_is_provided() {
        assertNotNull(this.menuId);
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        assertNotNull(this.action);
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_navigate_menu_cmd_command_is_executed() {
        try {
            NavigateMenuCmd cmd = new NavigateMenuCmd(sessionId, menuId, action);
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("menu.navigated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We expect either an IllegalStateException (invariant violation) or UnknownCommandException
        assertTrue(thrownException instanceof IllegalStateException || 
                   thrownException instanceof IllegalArgumentException ||
                   thrownException instanceof UnknownCommandException);
    }
}
