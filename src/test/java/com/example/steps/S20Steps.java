package com.example.steps;

import com.example.domain.session.model.EndSessionCmd;
import com.example.domain.session.model.SessionEndedEvent;
import com.example.domain.session.model.TellerSession;
import com.example.domain.session.model.TellerSessionAggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class S20Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        String sessionId = "TS-123";
        String tellerId = "T-01";
        Instant now = Instant.now();
        
        // Simulate an active session by creating the aggregate and applying a session started event logic internally
        aggregate = new TellerSessionAggregate(sessionId);
        
        // Manually hydrating the aggregate to a 'STARTED' state for testing purposes
        // In a real scenario, this would be loaded from an event store.
        // We use reflection or package-private setup if needed, but here we assume a valid state.
        // Since we are testing EndSession, we assume the session is currently ACTIVE.
        // To do this cleanly without exposing setters, we can assume the constructor
        // or a factory method handles initialization, or we test that an inactive session fails.
        
        // However, to strictly follow the pattern of other aggregates in the codebase,
        // we rely on the aggregate's internal state. 
        // Since TellerSession doesn't expose a 'start' command in this ticket, 
        // we simulate the state via a test helper or reflection, but TellerSession has
        // specific state requirements.
        
        // We will use a Test-Specific subclass or just trust that the 'valid' scenario passes
        // the invariants. The aggregate should start in a clean state, but we need it ACTIVE.
        
        // Let's assume the TellerSession starts in a null state. 
        // To make it 'valid' for ending, it must be authenticated and active.
        // Since we cannot execute a StartSessionCmd (out of scope/not defined), we simulate the state.
    }

    @Given("a valid sessionId is provided")
    public void a_valid_session_id_is_provided() {
        // The sessionId is handled in the constructor of the aggregate (e.g., "TS-123")
        // or passed in the command if the command wasn't bound to the aggregate instance.
        // Assuming the aggregate instance represents the session.
        
        // Hydrating the aggregate to a valid state so it doesn't reject immediately on invariants.
        // We assume the aggregate was loaded from the repo in an active state.
        // For this test harness, we simulate that:
        aggregate = new TellerSessionAggregate("TS-123") {
            @Override
            public boolean isActive() { return true; }
            @Override
            public boolean isAuthenticated() { return true; }
            @Override
            public boolean isExpired() { return false; }
            @Override
            public String getCurrentScreen() { return "MAIN_MENU"; }
        };
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate("TS-401") {
            @Override
            public boolean isActive() { return true; }
            @Override
            public boolean isAuthenticated() { return false; } // Violation
            @Override
            public boolean isExpired() { return false; }
            @Override
            public String getCurrentScreen() { return "MAIN_MENU"; }
        };
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate("TS-408") {
            @Override
            public boolean isActive() { return true; }
            @Override
            public boolean isAuthenticated() { return true; }
            @Override
            public boolean isExpired() { return true; } // Violation
            @Override
            public String getCurrentScreen() { return "MAIN_MENU"; }
        };
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate("TS-500") {
            @Override
            public boolean isActive() { return true; }
            @Override
            public boolean isAuthenticated() { return true; }
            @Override
            public boolean isExpired() { return false; }
            @Override
            public String getCurrentScreen() { return null; } // Violation (inconsistent state)
        };
    }

    @When("the EndSessionCmd command is executed")
    public void the_end_session_cmd_command_is_executed() {
        try {
            // The command payload is empty, but the type matters for dispatch.
            Command cmd = new EndSessionCmd();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a session.ended event is emitted")
    public void a_session_ended_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionEndedEvent);
        
        SessionEndedEvent event = (SessionEndedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.ended", event.type());
        Assertions.assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // We expect a runtime exception representing the domain error
        // (e.g., IllegalStateException or a custom DomainException)
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
    }
}
