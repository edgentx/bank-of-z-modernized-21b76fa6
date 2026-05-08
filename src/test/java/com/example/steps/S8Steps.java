package com.example.steps;

import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private List generatedEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_Statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-123");
        this.aggregate.setAccountNumber("acct-456");
        this.aggregate.setPeriodEnd(LocalDate.now().minusMonths(1));
        this.aggregate.setPreviousClosingBalance(new BigDecimal("100.00"));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled implicitly in aggregate setup
    }

    @And("a valid periodEnd is provided")
    public void a_valid_periodEnd_is_provided() {
        // Handled implicitly in aggregate setup
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_GenerateStatementCmd_command_is_executed() {
        try {
            var cmd = new GenerateStatementCmd(
                aggregate.id(),
                aggregate.getAccountNumber(),
                aggregate.getPeriodEnd(),
                aggregate.getPreviousClosingBalance()
            );
            this.generatedEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(generatedEvents);
        Assertions.assertEquals(1, generatedEvents.size());
        Assertions.assertTrue(generatedEvents.get(0) instanceof StatementGeneratedEvent);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-123");
        this.aggregate.setAccountNumber("acct-456");
        // Period ending in the far past implies the window is closed
        this.aggregate.setPeriodEnd(LocalDate.of(2020, 1, 1)); 
        this.aggregate.setPreviousClosingBalance(BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception but none was thrown");
        // Optionally check specific exception message or type
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance_match() {
        this.aggregate = new StatementAggregate("stmt-123");
        this.aggregate.setAccountNumber("acct-456");
        this.aggregate.setPeriodEnd(LocalDate.now());
        // Simulating a state where previous closing balance is tracked as 100.00
        // but the command attempts to open with 0.00
        this.aggregate.setPreviousClosingBalance(new BigDecimal("100.00")); 
        
        // Override the command execution step logic for this specific scenario context
        // Note: In a real robust framework, we might use a scenario context object, 
        // but for this simple test, we handle it in the 'When' or via a flag. 
        // However, the logic relies on the command payload. 
        // Let's assume the aggregate's 'execute' validates against internal state.
        
        // Since the Command carries the opening balance, we need to pass a 'wrong' one in the @When step.
        // This Given step sets up the Aggregate's expectation of what the balance *should* be.
    }
    
    // Helper to execute with wrong balance (called from glue or via reflection in advanced setups)
    // We will modify the @When slightly or use a specific scenario hook, but to keep it simple,
    // we will assume the standard @When is used, but the Command is constructed with mismatched data.
    // However, the standard @When uses aggregate.getPreviousClosingBalance(). 
    // So we must ensure the Command creates a mismatch.
    
    // Re-defining the behavior for this specific scenario context:
    @When("the GenerateStatementCmd command is executed with mismatched opening balance")
    public void the_GenerateStatementCmd_command_is_executed_with_mismatch() {
        // Aggregate expects 100.00 (set in Given), but we send 50.00
        try {
            var cmd = new GenerateStatementCmd(
                aggregate.id(),
                aggregate.getAccountNumber(),
                aggregate.getPeriodEnd(),
                new BigDecimal("50.00") // Mismatch!
            );
            this.generatedEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }
    
    // We need to link the specific scenario "Opening balance..." to this 'When'.
    // Since Gherkin scenarios are isolated, and the user requested strict Gherkin mapping,
    // and the previous 'When' matches the text, let's override the step implementation for that specific scenario
    // by checking the aggregate state or using a custom flag.
    
    // Actually, a cleaner way for the test:
    // The Given sets the 'previous' balance in the aggregate to 100.
    // The When (generic) uses the aggregate's getter, which returns 100.
    // This passes validation (100 == 100). 
    // 
    // To force a failure, the Given step must corrupt the data or the When step must be specific.
    // Given the constraint "Do not modify text", and that there is no mismatched 'When' text in the feature,
    // I will assume the aggregate logic or the 'Given' setup implies the violation naturally
    // or I will interpret the 'When' text to detect the violation context.
    // 
    // However, the cleanest implementation for the mismatch is to provide a dedicated When step
    // which I did above, but the Feature file uses the standard text.
    // So I will add logic to the standard When step:
    
    // Redefining the standard @When to be smarter:
    /* 
    @When("the GenerateStatementCmd command is executed")
    public void the_GenerateStatementCmd_command_is_executed() {
        try {
            // Logic to detect if we are in the "mismatch" scenario
            BigDecimal openingBalanceToUse = aggregate.getPreviousClosingBalance(); 
            // Heuristic: If period is valid but we want to trigger mismatch, 
            // we need a way to know. 
            // Without a context object, we can't easily share state between Givens of different scenarios
            // unless we store it in the Step instance.
            
            // Let's introduce a transient field 'forcingMismatch' in this Step class.
        }
    }
    */
    
    // Revised Plan: I will use the dedicated When I created, and assume the Feature file
    // *should* have been specific, OR I will assume the standard text is used and I inject
    // the mismatch logic via a flag set in the Given.
    
    private boolean forceMismatch = false;
    
    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_violates_opening_balance() {
        forceMismatch = true;
        this.aggregate = new StatementAggregate("stmt-123");
        this.aggregate.setAccountNumber("acct-456");
        this.aggregate.setPeriodEnd(LocalDate.now());
        this.aggregate.setPreviousClosingBalance(new BigDecimal("100.00"));
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_GenerateStatementCmd_command_is_executed_generic() {
        try {
            BigDecimal openingBalance = forceMismatch 
                ? new BigDecimal("50.00") 
                : aggregate.getPreviousClosingBalance();
                
            var cmd = new GenerateStatementCmd(
                aggregate.id(),
                aggregate.getAccountNumber(),
                aggregate.getPeriodEnd(),
                openingBalance
            );
            this.generatedEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        } finally {
            forceMismatch = false; // reset
        }
    }
}
