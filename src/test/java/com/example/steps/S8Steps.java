package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helper to create a valid base command
    private GenerateStatementCmd createValidCommand(String id) {
        return new GenerateStatementCmd(
                id,
                "ACC-123",
                Instant.now().minusSeconds(86400),
                Instant.now(),
                BigDecimal.ZERO,
                BigDecimal.valueOf(100.50)
        );
    }

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-valid-1");
        // Setup state to allow valid generation
        aggregate.setLastKnownClosingBalance(BigDecimal.ZERO);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in the @When clause via createValidCommand()
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Handled in the @When clause via createValidCommand()
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            GenerateStatementCmd cmd = createValidCommand(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent, "Event type should be StatementGeneratedEvent");
        assertEquals("statement.generated", resultEvents.get(0).type());
    }

    // --- Failure Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-closed-1");
        aggregate.setLastKnownClosingBalance(BigDecimal.ZERO);
        
        // Execute once to 'close' the period
        GenerateStatementCmd initialCmd = createValidCommand("stmt-closed-1");
        aggregate.execute(initialCmd);
        
        // Now aggregate is in 'generated' state, attempting to run again violates the invariant
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-balance-1");
        // Set a specific previous closing balance
        aggregate.setLastKnownClosingBalance(BigDecimal.valueOf(50.00));
        // The helper createValidCommand uses ZERO as opening, which violates the invariant
    }

    @When("the GenerateStatementCmd command is executed on violating aggregate")
    public void the_generate_statement_cmd_command_is_executed_on_violating_aggregate() {
        try {
            // Use the helper, but note that for the balance scenario, the helper creates a mismatch
            GenerateStatementCmd cmd = createValidCommand(aggregate.id()); 
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
    }
}
