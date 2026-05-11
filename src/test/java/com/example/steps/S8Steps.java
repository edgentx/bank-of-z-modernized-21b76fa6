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
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S8Steps {
    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in When
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Handled in When
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default valid command for the positive path
        if (cmd == null) {
            cmd = new GenerateStatementCmd(
                "stmt-123", 
                "acc-456", 
                "2023-10-31", 
                new BigDecimal("100.00"), 
                new BigDecimal("500.00"), 
                true // closed period
            );
        }
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        Assertions.assertEquals("stmt-123", event.statementId());
        Assertions.assertEquals("acc-456", event.accountNumber());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-violate-period");
        // Setup command to fail: period is NOT closed
        cmd = new GenerateStatementCmd(
            "stmt-violate-period", 
            "acc-456", 
            "2023-10-31", 
            BigDecimal.ZERO, 
            BigDecimal.ZERO, 
            false // NOT closed -> triggers violation
        );
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-violate-balance");
        // Setup command to fail: negative opening balance (logic flag in aggregate)
        cmd = new GenerateStatementCmd(
            "stmt-violate-balance", 
            "acc-456", 
            "2023-10-31", 
            new BigDecimal("-50.00"), // Negative opening balance triggers mismatch logic
            new BigDecimal("100.00"), 
            true
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // We expect either IllegalArgumentException or IllegalStateException based on aggregate logic
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException
        );
    }
}
