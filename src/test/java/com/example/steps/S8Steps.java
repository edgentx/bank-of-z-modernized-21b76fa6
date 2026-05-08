package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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

    // Test State
    private StatementAggregate aggregate;
    private GenerateStatementCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Test Data Builders
    private String statementId = "stmt-123";
    private String accountNumber = "acc-456";
    private Instant periodStart;
    private Instant periodEnd;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal previousClosingBalance;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate(statementId);
        resetDefaults();
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number set in defaults
        assertNotNull(accountNumber);
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Period set in defaults (closed period = past)
        assertNotNull(periodEnd);
        assertTrue(periodEnd.isBefore(Instant.now()));
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            command = new GenerateStatementCmd(
                statementId,
                accountNumber,
                periodStart,
                periodEnd,
                openingBalance,
                closingBalance,
                previousClosingBalance
            );
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof StatementGeneratedEvent, "Event should be StatementGeneratedEvent");
        
        StatementGeneratedEvent stmtEvent = (StatementGeneratedEvent) event;
        assertEquals("statement.generated", stmtEvent.type());
        assertEquals(statementId, stmtEvent.aggregateId());
        assertEquals(accountNumber, stmtEvent.accountNumber());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        // Setup valid aggregate
        aggregate = new StatementAggregate(statementId);
        resetDefaults();

        // Violation: Generate the first statement successfully
        try {
            GenerateStatementCmd firstCmd = new GenerateStatementCmd(
                statementId, accountNumber, periodStart, periodEnd,
                openingBalance, closingBalance, previousClosingBalance
            );
            aggregate.execute(firstCmd);
        } catch (Exception e) {
            fail("Setup failed: First generation should succeed");
        }
        
        // Violation: Try to generate again (Retroactive alteration)
        // The state 'generated' is now true in the aggregate.
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate(statementId);
        resetDefaults();

        // Violation: Mismatch opening and previous closing
        this.openingBalance = new BigDecimal("500.00"); // Prev closing is 100.00
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        // Check for the specific business logic exceptions (IllegalStateException or IllegalArgumentException)
        assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Exception should be a domain rule violation (IllegalStateException or IllegalArgumentException)"
        );
    }

    // Helper methods
    private void resetDefaults() {
        // Defaults ensure valid state unless overridden by Given steps
        periodStart = Instant.now().minusSeconds(3600 * 24 * 30); // 30 days ago
        periodEnd = Instant.now().minusSeconds(3600 * 24); // 1 day ago (Closed period)
        previousClosingBalance = new BigDecimal("100.00");
        openingBalance = new BigDecimal("100.00"); // Matches
        closingBalance = new BigDecimal("150.00");
    }
}
