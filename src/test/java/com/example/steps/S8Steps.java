package com.example.steps;

import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private String accountNumber;
    private LocalDate periodEnd;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-123");
        this.accountNumber = "acct-456";
        this.periodEnd = LocalDate.now().minusMonths(1);
        this.openingBalance = BigDecimal.ZERO;
        this.closingBalance = new BigDecimal("100.00");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // accountNumber initialized in Given step
        assertNotNull(accountNumber);
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // periodEnd initialized in Given step
        assertNotNull(periodEnd);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                aggregate.id(),
                accountNumber,
                openingBalance,
                closingBalance,
                periodEnd
            );
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertFalse(aggregate.uncommittedEvents().isEmpty());
        assertTrue(aggregate.uncommittedEvents().get(0) instanceof StatementGeneratedEvent);
        StatementGeneratedEvent event = (StatementGeneratedEvent) aggregate.uncommittedEvents().get(0);
        assertEquals(accountNumber, event.accountNumber());
        assertEquals(closingBalance, event.closingBalance());
    }

    // Negative Scenarios

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-retro-123");
        this.accountNumber = "acct-456";
        // Attempting to generate for a future date (not closed) or logic that checks period status
        this.periodEnd = LocalDate.now().plusDays(1); // Not closed yet
        this.openingBalance = BigDecimal.ZERO;
        this.closingBalance = new BigDecimal("100.00");
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        this.aggregate = new StatementAggregate("stmt-bal-123");
        this.accountNumber = "acct-456";
        this.periodEnd = LocalDate.now().minusMonths(1);
        // Opening balance does not match expected previous closing (let's assume previous was 100)
        this.openingBalance = new BigDecimal("50.00"); 
        this.closingBalance = new BigDecimal("100.00");
        // Mocking the 'previous' closing balance context would be in the repo, but for aggregate logic:
        // We pass the expected previous closing balance in the command or state. 
        // Here we simulate the check by assuming the command carries the 'previous' context or aggregate tracks it.
        // For this step, we'll assume the command is constructed with a mismatch.
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
