package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Helper to reset state
    private void initializeAggregate() {
        this.aggregate = new TellerSessionAggregate("session-1");
        this.capturedException = null;
        this.resultEvents = null;
    }

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        initializeAggregate();
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Context setup handled in When block
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Context setup handled in When block
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        initializeAggregate();
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        initializeAggregate();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation() {
        initializeAggregate();
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        // Determine context based on Gherkin state
        // If we are in a violation scenario, we check which one via flags or exception context.
        // However, Cucumber steps are sequential. We need to map the scenario title/context to the command payload.
        // Since Cucumber doesn't pass state directly, we rely on the specific Given used.
        // A simple way is to try/catch or set a mode. But here we can derive from the aggregate state if it had state.
        // Since aggregate is fresh every time, we use thread local or similar pattern. 
        // Simpler: Just use a default valid command, and override specific fields based on the scenario context if we stored it.
        // For simplicity in this BDD framework, we check the specific Givens.
        
        boolean isAuthenticated = true;
        boolean isTimedOut = false;
        String navigationState = "initialContext";

        // Heuristic to detect which Given was called based on exception expected or internal state
        // Ideally, we'd store "expectedOutcome" in the Given. 
        // Given the constraints, we'll construct a valid command by default. 
        // If the test is for a specific violation, we must have stored that intent.
        // Let's rely on the fact that the violation Givens are distinct.
        // Actually, the best way is to read the scenario context, but here we can just check: 
        // if we are looking for a domain error, we might need to trigger it.
        // But we need deterministic behavior.
        
        // Let's refine the Givens to store the violation mode.
    }

    // Overriding the When method to be more specific to the Givens
    // We will define specific Whens or handle logic here. 
    // To keep it clean, let's assume we use the 'execute' method with specific params based on the scenario.
    // Since Cucumber Java doesn't support parameterized Givens easily without tables, we will check
    // the aggregate's internal state or specific flags set by the Given.
    
    // Better approach for this implementation:
    // The Given methods will set "currentCommandConfig" fields.

    private String cmdTellerId = "teller-123";
    private String cmdTerminalId = "term-456";
    private boolean cmdAuth = true;
    private boolean cmdTimeout = false;
    private String cmdNav = "initialContext";

    @Given("a valid tellerId is provided")
    public void setValidTellerId() { this.cmdTellerId = "teller-123"; }

    @Given("a valid terminalId is provided")
    public void setValidTerminalId() { this.cmdTerminalId = "term-456"; }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void violatesAuth() {
        initializeAggregate();
        this.cmdAuth = false;
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void violatesTimeout() {
        initializeAggregate();
        this.cmdTimeout = true;
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void violatesNav() {
        initializeAggregate();
        this.cmdNav = "invalidContext";
    }

    @When("the StartSessionCmd command is executed")
    public void executeStartSessionCmd() {
        try {
            StartSessionCmd cmd = new StartSessionCmd(
                aggregate.id(), 
                cmdTellerId, 
                cmdTerminalId, 
                cmdAuth, 
                cmdTimeout, 
                cmdNav
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
        SessionStartedEvent event = (SessionStartedEvent) resultEvents.get(0);
        Assertions.assertEquals("session.started", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // Verify it's an IllegalStateException as per domain logic
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }
}
