package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private StatementAggregate.PreviousStatementChecker mockChecker;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Helper to create a basic valid command
    private GenerateStatementCmd createValidCommand(String accountNumber) {
        return new GenerateStatementCmd(
                UUID.randomUUID().toString(),
                accountNumber,
                Instant.now().minusSeconds(86400), // Yesterday
                new BigDecimal("100.00"),
                new BigDecimal("150.00")
        );
    }

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // Setup a valid checker that returns null (no previous statement)
        mockChecker = (acc, date) -> null;
        aggregate = new StatementAggregate(UUID.randomUUID().toString(), mockChecker);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Placeholder: In a real scenario, this might set a context variable.
        // Here, we assume the command created in the When step uses a hardcoded valid account.
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Placeholder: Valid period is in the past, handled in When step.
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        GenerateStatementCmd cmd = createValidCommand("ACC-123");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        assertEquals("statement.generated", resultEvents.get(0).type());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        // Violation 1: Aggregate already generated (Retroactive alteration)
        String id = UUID.randomUUID().toString();
        // Use a no-op checker
        mockChecker = (acc, date) -> null;
        aggregate = new StatementAggregate(id, mockChecker);
        
        // Execute a valid command first to put it in generated state
        GenerateStatementCmd firstCmd = new GenerateStatementCmd(
                id, "ACC-123", Instant.now().minusSeconds(1000), BigDecimal.ZERO, BigDecimal.ZERO
        );
        aggregate.execute(firstCmd);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        // Violation 2: Opening balance mismatch
        // Setup a checker that returns a different previous closing balance
        mockChecker = (acc, date) -> new BigDecimal("500.00"); // Previous close was 500
        aggregate = new StatementAggregate(UUID.randomUUID().toString(), mockChecker);
        // Note: The command used in the corresponding When step must provide a mismatching opening balance (e.g., 100)
        // We handle this by creating a specific context or interpreting the violation scenario.
        // Since the When step is shared, we use a flag or specific setup in the step below if needed.
        // However, to fit the pattern, we assume the violation setup prepares the AGGREGATE context such that
        // the standard command execution would fail.
    }

    // Custom When for the balance mismatch scenario to ensure we trigger the specific error
    // Alternatively, we could check the violation type in the generic When step, but custom steps are clearer.
    // But the prompt asks to use the scenarios AS-IS. So we must use the generic When.
    // To make the generic When fail for balance mismatch, we need to ensure the command passed
    // has a mismatched balance relative to our mockChecker.
    
    // Overriding the command generation for the specific violation scenario is tricky in pure Cucumber 
    // without stateful context. We will use a thread-local or instance variable flag.
    
    private boolean forceMismatch = false;

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void setup_balance_mismatch_scenario() {
        forceMismatch = true;
        // Setup checker that expects 500
        mockChecker = (acc, date) -> new BigDecimal("500.00");
        aggregate = new StatementAggregate(UUID.randomUUID().toString(), mockChecker);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed_general() {
        GenerateStatementCmd cmd;
        if (forceMismatch) {
            // Create command with 100, but checker returns 500
            cmd = new GenerateStatementCmd(
                aggregate.id(),
                "ACC-123", 
                Instant.now().minusSeconds(86400), 
                new BigDecimal("100.00"), // Mismatch
                new BigDecimal("150.00")
            );
            forceMismatch = false; // reset
        } else {
            cmd = createValidCommand("ACC-123");
        }
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain errors are usually IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
        assertNull(resultEvents, "No events should be produced on failure");
    }
}
