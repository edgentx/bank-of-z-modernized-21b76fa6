package com.example.steps;

import com.example.domain.shared.Command;
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

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String sessionId = "sess-123";
    private String tellerId = "teller-01";
    private String terminalId = "term-42";
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // --- Givens ---

    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate(sessionId);
    }

    @Given("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // Handled in setup
    }

    @Given("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // Handled in setup
    }

    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_that_violates_authentication() {
        aggregate = new TellerSessionAggregate(sessionId);
        // The violation is implicit: we will send isAuthenticated=false in the command
    }

    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_that_violates_timeout() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Force the aggregate into a state where it thinks a session is active but timed out
        // or where it cannot start due to previous timeout constraints.
        // For this BDD scenario, we simulate the aggregate knowing it's invalid.
        aggregate.forceTimeoutViolation();
    }

    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_that_violates_navigation_state() {
        aggregate = new TellerSessionAggregate(sessionId);
        // Force an invalid state (e.g., "TRANSACTION_PENDING") instead of "IDLE"
        aggregate.forceNavigationStateViolation("TRANSACTION_PENDING");
    }

    // --- Whens ---

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            // Default valid command data
            boolean authenticated = true;
            String navState = "IDLE";
            
            // If we are in the violation scenarios, adjust command accordingly
            // 1. Auth violation: Send false
            if (capturedException == null && aggregate.getClass().getSimpleName().contains("Mock")) {
               // Logic handled via specific dispatch or command properties below
            }
            
            // Detecting which scenario we are in to craft the violating command
            // Note: In a real Step Defs, we might use context flags. Here we infer or set flags.
            if (aggregate.toString().contains("TIMEOUT")) { 
               // We can't easily infer the violation type from the aggregate object without state getters.
               // We will rely on the specific Given methods setting internal flags or we use a context variable.
               // For simplicity, we check the internal state or use a dedicated execution method.
               // However, the standard Cucumber pattern is to use context.
               // Let's assume standard execution first, and catch exceptions.
            }
            
            Command cmd = createCommandBasedOnScenario();
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // --- Thens ---

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // In this domain implementation, violations throw IllegalStateException
        Assertions.assertTrue(capturedException instanceof IllegalStateException);
    }

    // --- Helpers ---

    private Command createCommandBasedOnScenario() {
        // Check for Auth Violation Scenario
        // We can infer this if the aggregate is fresh but we expect failure.
        // A more robust way is checking stack trace or context, but let's inspect the aggregate state if possible,
        // or rely on the fact that we set specific 'violation' flags in the Given steps.
        
        // Since we can't easily pass context between steps without a shared Context class,
        // and we want to keep it simple: We'll look at the aggregate state if accessible, or
        // assume specific scenarios trigger specific exception messages.
        
        // Strategy: The 'Given' methods set up the Aggregate. The command must be the *trigger*.
        // But for 'Teller must be authenticated', the violation is in the Command payload, not just the Aggregate state.
        // For Timeout, it is Aggregate state. For Nav, it is Aggregate state.
        
        // Hack for S-18: We check if the aggregate is in a 'forced' bad state for Nav/Timeout.
        // If it looks normal, we check if we are testing Auth.
        // Actually, the 'Given' for Auth just creates the aggregate. The violation happens when we send the command.
        
        // Let's assume the standard valid command, but override fields based on specific test context detection.
        // Since we don't have a formal Context class, let's assume if we are in 'the_command_is_rejected', we vary the command.
        // But that's unreliable.
        
        // Better approach: Use a simple variable stored in this class.
        boolean testAuth = false;
        
        // Let's try-catch or assume. Actually, the simplest way for this generated code is to check
        // if the aggregate is 'new' and we haven't forced other violations.
        
        // Let's just return the VALID command. If the test expects rejection, we must have called the Given that sets up the violation.
        // 1. Auth: Command is invalid. Aggregate is valid.
        // 2. Timeout: Aggregate is invalid (timed out). Command is valid.
        // 3. Nav: Aggregate is invalid (bad state). Command is valid.
        
        // We need to know which Scenario we are running.
        // Workaround: The 'Given' for Auth doesn't touch the aggregate differently than a valid one.
        // We need a flag.
    }
    
    // We will implement the execution method to be smart or use the Scenario name if available? No.
    // We will rely on the fact that the steps are called in order.
    
    private boolean isAuthScenario = false;
    
    // We can hook into the Given to set flags.
    // But the prompt provided specific Given signatures.
    
    // Let's refine the "the_start_session_cmd..." method.
    // We will check the aggregate's internal state (or lack thereof) to guess.
    // Actually, the cleanest way is:
    
    @Given("A teller must be authenticated to initiate a session.")
    public void a_teller_must_be_authenticated() {
        isAuthScenario = true;
    }
    
    // Since Cucumber matches regex, the long Given text in the prompt is unique.
    
    // Overriding the specific Given for Auth to set the flag
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void setupAuthScenario() {
        aggregate = new TellerSessionAggregate(sessionId);
        isAuthScenario = true;
    }
    
    // We need a 'generic' command builder.
    private Command createCommandBasedOnScenario() {
        boolean authenticated = true;
        String navState = "IDLE";
        
        if (isAuthScenario) {
            authenticated = false;
        }
        
        // For Nav/Timeout, the Aggregate is pre-corrupted in the Given steps.
        // The command itself can be valid, but the Aggregate.execute will throw.
        return new StartSessionCmd(sessionId, tellerId, terminalId, authenticated, navState);
    }

}
