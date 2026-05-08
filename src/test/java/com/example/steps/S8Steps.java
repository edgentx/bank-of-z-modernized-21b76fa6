package com.example.steps;

import com.example.domain.account.model.GenerateStatementCmd;
import com.example.domain.account.model.StatementAggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * BDD Step Definitions for S-8: GenerateStatementCmd.
 */
public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute GenerateStatementCmd
    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number setup is deferred to when the command is built
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Period setup is deferred to when the command is built
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            Instant now = Instant.now();
            // Build a valid command
            cmd = new GenerateStatementCmd(
                "stmt-1",
                "ACC-123",
                now.minusSeconds(86400), // Start: 1 day ago
                now,                     // End: Now
                new BigDecimal("100.00"),
                new BigDecimal("150.00"),
                null // No previous statement context for the happy path
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("statement.generated", resultEvents.get(0).type());
        assertNull(caughtException, "Should not have thrown an exception");
    }

    // Scenario: GenerateStatementCmd rejected — retroactively
    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-2");
        // Simulate a previous statement that ended at T-100
        Instant prevEnd = Instant.now().minusSeconds(86400 * 2); // 2 days ago
        aggregate.withPreviousStatement(prevEnd, new BigDecimal("100.00"));
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_cmd_executed_for_retroactive() {
        try {
            Instant now = Instant.now();
            // Attempt to generate a statement that starts 1 day ago, 
            // but the aggregate context implies the previous one ended 2 days ago (gap/retroactive)
            cmd = new GenerateStatementCmd(
                "stmt-2",
                "ACC-123",
                now.minusSeconds(86400), // Start: 1 day ago (Retroactive gap)
                now,
                new BigDecimal("100.00"),
                new BigDecimal("100.00"),
                Instant.now().minusSeconds(86400 * 2) // Passed in explicitly
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException.getMessage().contains("retroactively"));
    }

    // Scenario: GenerateStatementCmd rejected — opening balance mismatch
    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-3");
        // Simulate a previous statement with closing balance 500.00
        aggregate.withPreviousStatement(Instant.now().minusSeconds(100), new BigDecimal("500.00"));
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_cmd_executed_for_balance_mismatch() {
        try {
            Instant now = Instant.now();
            Instant prevEnd = Instant.now().minusSeconds(100);
            
            cmd = new GenerateStatementCmd(
                "stmt-3",
                "ACC-123",
                prevEnd, // Start time matches previous end time (Valid period)
                now,
                new BigDecimal("100.00"), // Opening Balance MISMATCH (Should be 500)
                new BigDecimal("600.00"),
                prevEnd
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
