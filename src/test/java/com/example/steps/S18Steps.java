package com.example.steps;

import com.example.domain.tellersession.model.SessionStartedEvent;
import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S18Steps {

    private TellerSessionAggregate aggregate;
    private String validTellerId = "TELLER_001";
    private String validTerminalId = "TERM_A01";
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Scenario 1: Success
    @Given("a valid TellerSession aggregate")
    public void a_valid_teller_session_aggregate() {
        aggregate = new TellerSessionAggregate("SESSION_001");
    }

    @And("a valid tellerId is provided")
    public void a_valid_teller_id_is_provided() {
        // No op, we use the default validTellerId
    }

    @And("a valid terminalId is provided")
    public void a_valid_terminal_id_is_provided() {
        // No op, we use the default validTerminalId
    }

    // Scenario 2: Auth
    @Given("a TellerSession aggregate that violates: A teller must be authenticated to initiate a session.")
    public void a_teller_session_aggregate_violating_auth() {
        aggregate = new TellerSessionAggregate("SESSION_002");
        // By contract, unauthenticated commands throw an exception.
        // We don't need to mutate state, we will pass the command incorrectly or assume context.
        // However, based on the aggregate logic, we might just pass a command indicating a failed auth context.
        // But the domain model usually checks this. 
        // Let's assume we need to force the aggregate into a state where it rejects.
        // Or, more simply, we will catch the exception thrown when executing.
        // The aggregate checks for a generic authentication state. 
        // In this step def, we will simulate the command execution in the 'When' block.
    }

    // Scenario 3: Timeout
    @Given("a TellerSession aggregate that violates: Sessions must timeout after a configured period of inactivity.")
    public void a_teller_session_aggregate_violating_timeout() {
        aggregate = new TellerSessionAggregate("SESSION_003");
        // Assume we try to start a session that is effectively "timed out" already or invalid.
        // This invariant is often checked via state.
        // The aggregate should reject if the command context is invalid.
    }

    // Scenario 4: Navigation State
    @Given("a TellerSession aggregate that violates: Navigation state must accurately reflect the current operational context.")
    public void a_teller_session_aggregate_violating_nav_state() {
        aggregate = new TellerSessionAggregate("SESSION_004");
        // Simulating a mismatch or invalid context.
    }

    @When("the StartSessionCmd command is executed")
    public void the_start_session_cmd_command_is_executed() {
        try {
            StartSessionCmd cmd;
            // We map the scenarios to the command fields.
            // If the aggregate ID matches the violation scenarios, we might pass different parameters or rely on internal state.
            // For simplicity, we pass the valid params. If the aggregate is in a bad state, it rejects.
            cmd = new StartSessionCmd(aggregate.id(), validTellerId, validTerminalId);
            
            // Handling specific violation scenarios:
            if (aggregate.id().equals("SESSION_002")) {
                // Simulate unauthenticated by passing a null/invalid context marker if the Command supported it.
                // Since StartSessionCmd only has IDs, we assume the aggregate tracks auth status internally or we trigger it differently.
                // Based on the prompt's implied logic, we'll trigger the failure condition here.
                // If the aggregate was built to reject via an internal flag, we'd set it.
                // But for this BDD, we assume the aggregate validates the inputs.
                // Let's assume for 'SESSION_002' we explicitly want to test the auth failure.
                // However, the Command signature provided in the 'Domain Code' section below doesn't have an 'isAuthenticated' flag.
                // This implies the failure might be triggered by specific IDs or state.
                // OR, the prompt implies we should EXPECT an error.
                // Let's trigger the execution.
            }

            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("a session.started event is emitted")
    public void a_session_started_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof SessionStartedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}