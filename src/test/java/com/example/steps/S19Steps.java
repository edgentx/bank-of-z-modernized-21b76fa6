package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.teller.model.*;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.teller.repository.InMemoryTellerSessionRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class S19Steps {

    private final TellerSessionRepository repository = new InMemoryTellerSessionRepository();
    private TellerSessionAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        // Create a session via the Initiate command to get it into a valid state
        String sessionId = "session-authenticated";
        InitiateTellerSessionCmd initCmd = new InitiateTellerSessionCmd(sessionId, "teller1", "terminal1");
        aggregate = new TellerSessionAggregate(sessionId);
        aggregate.execute(initCmd);
        // Aggregate is now in a valid, authenticated state.
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_not_authenticated() {
        // Create an aggregate that has not been initiated (or has been terminated)
        aggregate = new TellerSessionAggregate("session-unauthenticated");
        // State is: !authenticated. This violates the invariant for navigation.
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_timed_out() {
        String sessionId = "session-timeout";
        aggregate = new TellerSessionAggregate(sessionId);
        // Initialize it to a valid state first
        InitiateTellerSessionCmd initCmd = new InitiateTellerSessionCmd(sessionId, "teller1", "terminal1");
        aggregate.execute(initCmd);
        
        // Manually force the last active time to be ancient to simulate timeout 
        // (bypassing command validation for the sake of Given scenario setup)
        aggregate.simulateTimeOut(); 
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_invalid_context() {
        String sessionId = "session-bad-context";
        aggregate = new TellerSessionAggregate(sessionId);
        // Init
        InitiateTellerSessionCmd initCmd = new InitiateTellerSessionCmd(sessionId, "teller1", "terminal1");
        aggregate.execute(initCmd);

        // Force internal state to something impossible to represent the violation
        aggregate.simulateInvalidContext();
    }

    @Given("a valid sessionId is provided")
    public void a_valid_sessionId_is_provided() {
        // The aggregate is already initialized with a valid ID in the @Given steps
        assertNotNull(aggregate.id());
    }

    @Given("a valid menuId is provided")
    public void a_valid_menuId_is_provided() {
        // Data constraint checked in execution
    }

    @Given("a valid action is provided")
    public void a_valid_action_is_provided() {
        // Data constraint checked in execution
    }

    @When("the NavigateMenuCmd command is executed")
    public void the_NavigateMenuCmd_command_is_executed() {
        try {
            // Command data is valid per scenario requirements
            NavigateMenuCmd cmd = new NavigateMenuCmd(aggregate.id(), "MENU_MAIN", "ENTER");
            resultingEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (Exception e) {
            capturedException = e;
            resultingEvents = null;
        }
    }

    @Then("a menu.navigated event is emitted")
    public void a_menu_navigated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof MenuNavigatedEvent);
        MenuNavigatedEvent event = (MenuNavigatedEvent) resultingEvents.get(0);
        assertEquals("menu.navigated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // We expect either IllegalStateException or IllegalArgumentException based on the invariant violated
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
