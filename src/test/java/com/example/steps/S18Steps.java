package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.teller.model.SessionStartedEvent;
import com.example.domain.teller.model.StartSessionCmd;
import com.example.domain.teller.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.UUID;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private StartSessionCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid TellerSession aggregate")
    public void a_valid_TellerSession_aggregate() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_TellerSession_aggregate_that_violates_authentication() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Command will set isAuthenticated to false
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_TellerSession_aggregate_that_violates_timeout() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Command will set isTimedOut to true
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_TellerSession_aggregate_that_violates_navigation_state() {
        String sessionId = UUID.randomUUID().toString();
        aggregate = new TellerSessionAggregate(sessionId);
        // Command will set isNavigationStateValid to false
    }

    @And("a valid tellerId is provided")
    public void a_valid_tellerId_is_provided() {
        // Teller ID is handled in command construction below
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminalId_is_provided() {
        // Terminal ID is handled in command construction below
    }

    @When("the StartSessionCmd command is executed")
    public void the_StartSessionCmd_command_is_executed() {
        try {
            // Default valid attributes
            String tellerId = "TELLER-001";
            String terminalId = "TERM-101";
            boolean isAuthenticated = true;
            boolean isTimedOut = false;
            boolean isNavValid = true;

            // Adjust based on the scenario description if necessary (using context checks)
            if (aggregate != null && !aggregate.getClass().getSimpleName().isEmpty()) {
                // This is a simplification. In a real framework, we'd store scenario state explicitly.
                // Here we inspect the aggregate or use defaults. 
                // Since we don't have stateful context setup beyond the "Given" block creating the object, 
                // we rely on the specific violation setup method called in the Given steps.
                // However, the command holds the flags in this implementation.
                // Let's reset the state to invalid if the specific Given methods were called.
                // (In real Cucumber, we'd share state via a shared context object).
                
                // Simple heuristic check for demo purposes:
                // If the aggregate ID matches specific patterns or if we tracked state, we'd switch.
                // For this output, we assume the 'Given' step sets the state we need to pass to the command.
                // Since I can't easily distinguish *which* Given was called without a shared context,
                // I will assume the last 'Given' set up the state. 
                // To make this robust, we should check the exception type expected.
            }
            
            // Refining logic: We need a way to know which invariant to break.
            // Since S18Steps is instantiated per scenario, we can store the violation type in a field.
            // But let's assume valid defaults for now, and override if specific fields were set.
            // *Correction*: The prompt implies the *aggregate* violates it, but the Command carries the auth/timeout flags in this model.
            // I will assume the command needs to match the "Given".
            // Since I can't detect the "Given" text, I will assume the user expects valid execution unless specific logic exists.
            // To pass the specific negative tests, the Step Definition would need context.
            // I will construct the command based on a flag.
            
            if (caughtException != null) { // Hacky way to signal state, let's rely on defaults for now.
                // Actually, let's just construct valid defaults for the Happy Path.
                // The negative tests will need specific handling.
            }
            
            // FIX: The prompt implies the AGGREGATE has the state. But the aggregate is new in the Given.
            // The Command carries the checks.
            // I will set defaults to VALID.
            // If the test fails, we might need to adjust the 'When' to detect the scenario.
            // However, I will simulate the negative paths by looking at the aggregate state or using a shared field.
            // Let's use a simpler approach: The 'Given' methods for violations will set a 'violation' flag.

        } catch (Exception e) {
            // Handle construction failure
        }

        // Determine command parameters based on the "Given" called previously.
        // We check if the aggregate is in a state that implies a specific violation (not possible for fresh aggregates).
        // We rely on the Cucumber execution order.
        // Since we can't store state easily between steps without a field, we add fields.
    }

    // Fields to control the command construction based on the Given step
    private boolean shouldBeAuthenticated = true;
    private boolean shouldHaveTimedOut = false;
    private boolean shouldBeNavValid = true;

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupUnauthenticated() {
        a_valid_TellerSession_aggregate(); // Create aggregate
        this.shouldBeAuthenticated = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void setupTimedOut() {
        a_valid_TellerSession_aggregate();
        this.shouldHaveTimedOut = true;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void setupInvalidNav() {
        a_valid_TellerSession_aggregate();
        this.shouldBeNavValid = false;
    }

    // The actual When implementation using the fields
    @When("the StartSessionCmd command is executed")
    public void executeCommand() {
        cmd = new StartSessionCmd(
            "TELLER-001", 
            "TERM-101", 
            shouldBeAuthenticated, 
            shouldHaveTimedOut, 
            shouldBeNavValid
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
        Assertions.assertEquals("TELLER-001", event.tellerId());
        Assertions.assertEquals("TERM-101", event.terminalId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
