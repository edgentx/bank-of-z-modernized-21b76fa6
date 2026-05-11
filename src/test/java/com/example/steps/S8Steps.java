package com.example.steps;

import com.example.domain.shared.DomainEvent;
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
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Test Data Constants
    private static final String TEST_STATEMENT_ID = "stmt-123";
    private static final String TEST_ACCOUNT_NUMBER = "acct-456";
    private static final BigDecimal TEST_OPENING_BALANCE = new BigDecimal("100.00");
    private static final Instant NOW = Instant.now();
    private static final Instant PAST = NOW.minusSeconds(86400);

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        Assertions.assertNotNull(aggregate);
        Assertions.assertEquals(TEST_STATEMENT_ID, aggregate.id());
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Account number is provided in the command construction
    }

    @And("a valid periodEnd is provided")
    public void aValidPeriodEndIsProvided() {
        // Period end is provided in the command construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void theGenerateStatementCmdCommandIsExecuted() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            TEST_STATEMENT_ID,
            TEST_ACCOUNT_NUMBER,
            PAST,
            NOW,
            TEST_OPENING_BALANCE,
            TEST_OPENING_BALANCE // Matching balances for success case
        );
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void aStatementGeneratedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent stmtEvent = (StatementGeneratedEvent) event;
        Assertions.assertEquals("statement.generated", stmtEvent.type());
        Assertions.assertEquals(TEST_STATEMENT_ID, stmtEvent.aggregateId());
        Assertions.assertEquals(TEST_ACCOUNT_NUMBER, stmtEvent.accountNumber());
    }

    // Scenario 2 & 3 Setup & Execution Steps
    
    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        // We will trigger the violation in the 'When' step by setting a future date
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        // We will trigger the violation in the 'When' step by providing mismatching balances
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // We check for IllegalArgumentException or IllegalStateException as domain errors
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, but got: " + caughtException.getClass().getSimpleName()
        );
    }

    // Specific When steps for error cases (Overloading the When step usually isn't possible in standard Cucumber Java without regex, 
    // but here we rely on the Given setup modifying the context used by the generic When.
    // However, Cucumber Java requires distinct method signatures or regex to disambiguate.
    // I will add specific When steps for clarity).

    @When("the GenerateStatementCmd command is executed with a future periodEnd")
    public void theGenerateStatementCmdCommandIsExecutedWithFuturePeriodEnd() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            TEST_STATEMENT_ID,
            TEST_ACCOUNT_NUMBER,
            NOW,
            NOW.plusSeconds(3600), // Future date
            TEST_OPENING_BALANCE,
            TEST_OPENING_BALANCE
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the GenerateStatementCmd command is executed with mismatched balances")
    public void theGenerateStatementCmdCommandIsExecutedWithMismatchedBalances() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            TEST_STATEMENT_ID,
            TEST_ACCOUNT_NUMBER,
            PAST,
            NOW,
            new BigDecimal("50.00"), // Opening
            new BigDecimal("100.00")  // Previous Closing (Mismatch)
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // We need to ensure the scenarios map correctly. 
    // The generic 'When the GenerateStatementCmd command is executed' in Scenario 1 works. 
    // For Scenario 2 & 3, the Cucumber feature file provided uses the same 'When' line. 
    // To handle this strictly with one step definition method, we would need state flags. 
    // However, a cleaner way for this exercise is to rely on the feature file passing or assume the user updates the feature file 
    // to be specific. Given the instruction to 'Return ALL files', I will update the feature file 
    // to use specific When steps or inject logic into the single When step.
    
    // Re-reading the instructions: 'Use the acceptance criteria AS-IS for the Gherkin scenarios'.
    // So I cannot change S-8.feature. I must make the step definition handle the ambiguity.
    
    // Implementation Strategy: Detect the state set by the Given steps.
    
    private enum TestScenario { SUCCESS, CLOSED_PERIOD_VIOLATION, BALANCE_VIOLATION }
    private TestScenario currentScenario;

    @Given("a valid Statement aggregate")
    public void initValidScenario() {
        this.currentScenario = TestScenario.SUCCESS;
        aValidStatementAggregate();
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void initClosedPeriodViolationScenario() {
        this.currentScenario = TestScenario.CLOSED_PERIOD_VIOLATION;
        aStatementAggregateThatViolatesClosedPeriod();
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void initBalanceViolationScenario() {
        this.currentScenario = TestScenario.BALANCE_VIOLATION;
        aStatementAggregateThatViolatesOpeningBalance();
    }

    @When("the GenerateStatementCmd command is executed")
    public void theCommandIsExecutedDispatcher() {
        if (currentScenario == TestScenario.CLOSED_PERIOD_VIOLATION) {
            theGenerateStatementCmdCommandIsExecutedWithFuturePeriodEnd();
        } else if (currentScenario == TestScenario.BALANCE_VIOLATION) {
            theGenerateStatementCmdCommandIsExecutedWithMismatchedBalances();
        } else {
            theGenerateStatementCmdCommandIsExecuted(); // Standard success path
        }
    }
}
