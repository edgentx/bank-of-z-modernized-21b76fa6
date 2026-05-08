package com.example.steps;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is part of the command constructed in the next steps
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Period end is part of the command
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default valid command setup for the positive scenario
        if (cmd == null) {
            cmd = new GenerateStatementCmd(
                "stmt-123",
                "acc-456",
                Instant.now(),
                BigDecimal.valueOf(100.50),
                BigDecimal.valueOf(200.00),
                Optional.empty(),
                Optional.empty(),
                true // Period is closed
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
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        assertEquals("statement.generated", resultEvents.get(0).type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-violate-period");
        // Setting up command with isPeriodClosed = false to trigger the violation
        cmd = new GenerateStatementCmd(
            "stmt-violate-period",
            "acc-456",
            Instant.now(),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            Optional.empty(),
            Optional.empty(),
            false // VIOLATION: Period is open
        );
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_mismatch() {
        aggregate = new StatementAggregate("stmt-violate-balance");
        // Setting up command where opening (50) != previous closing (60)
        cmd = new GenerateStatementCmd(
            "stmt-violate-balance",
            "acc-456",
            Instant.now(),
            new BigDecimal("50.00"), // Opening
            new BigDecimal("70.00"), // Closing
            Optional.of(Instant.now().minusSeconds(86400)), // Previous Period End
            Optional.of(new BigDecimal("60.00")), // Previous Closing
            true // Period is closed
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
