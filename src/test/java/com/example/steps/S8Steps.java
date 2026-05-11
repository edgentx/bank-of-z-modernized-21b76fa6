package com.example.steps;

import com.example.domain.statement.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;
    private String statementId = "stmt-123";

    @Given("a valid Statement aggregate")
    public void a_valid_Statement_aggregate() {
        aggregate = new StatementAggregate(statementId);
        // Assume no prior history for a happy path
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Context setup - usually handled in the When step via Command
    }

    @And("a valid periodEnd is provided")
    public void a_valid_periodEnd_is_provided() {
        // Context setup - usually handled in the When step via Command
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_GenerateStatementCmd_command_is_executed() {
        // Default valid values for happy path
        the_GenerateStatementCmd_command_is_executed_with("acct-1", LocalDate.of(2023, 10, 31), BigDecimal.valueOf(100.00), BigDecimal.valueOf(150.00));
    }

    @When("the GenerateStatementCmd command is executed with accountNumber {string}, periodEnd {string}, openingBalance {double}, and closingBalance {double}")
    public void the_GenerateStatementCmd_command_is_executed_with(String acct, String date, double openBal, double closeBal) {
         the_GenerateStatementCmd_command_is_executed_with(acct, LocalDate.parse(date), BigDecimal.valueOf(openBal), BigDecimal.valueOf(closeBal));
    }

    private void the_GenerateStatementCmd_command_is_executed_with(String accountNumber, LocalDate periodEnd, BigDecimal openingBalance, BigDecimal closingBalance) {
        try {
            GenerateStatementCmd cmd = new GenerateStatementCmd(statementId, accountNumber, periodEnd, openingBalance, closingBalance, BigDecimal.valueOf(200.00)); // previousClosing = 200 to fail mismatch
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof StatementGeneratedEvent);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_Statement_aggregate_that_violates_retroactive_alteration() {
        aggregate = new StatementAggregate(statementId);
        // Simulate state where a statement for this period already exists
        aggregate.apply(new StatementGeneratedEvent(statementId, "acct-1", LocalDate.of(2023, 10, 31), BigDecimal.ZERO, BigDecimal.ZERO, LocalDate.now()));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_Statement_aggregate_that_violates_opening_balance_mismatch() {
        aggregate = new StatementAggregate(statementId);
        // Simulate previous statement closing balance of 1000
        aggregate.apply(new StatementGeneratedEvent(statementId, "acct-1", LocalDate.of(2023, 9, 30), BigDecimal.ZERO, new BigDecimal("1000.00"), LocalDate.now()));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // Ideally catch specific DomainException, but runtime works for generic BDD checks
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

    @When("the GenerateStatementCmd command is executed with mismatched opening balance")
    public void the_GenerateStatementCmd_command_is_executed_with_mismatched_opening_balance() {
        // Previous close was 1000 (set in Given), providing 900 here
        GenerateStatementCmd cmd = new GenerateStatementCmd(statementId, "acct-1", LocalDate.of(2023, 10, 31), new BigDecimal("900.00"), new BigDecimal("1200.00"), null);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }
}
