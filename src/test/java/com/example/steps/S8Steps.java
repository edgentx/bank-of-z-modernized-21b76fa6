package com.example.steps;

import com.example.domain.shared.Command;
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

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private String accountId = "ACC-12345";
    private Instant periodEnd;
    private BigDecimal openingBalance = new BigDecimal("100.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("STMT-TEST-1");
        periodEnd = Instant.now().minusSeconds(86400); // Yesterday
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // accountId initialized
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // periodEnd initialized
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            Command cmd = new GenerateStatementCmd(
                    "STMT-TEST-1",
                    accountId,
                    periodEnd,
                    openingBalance,
                    null // No previous balance context for simple happy path
            );
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have uncommitted events");
        DomainEvent event = events.get(0);
        assertTrue(event instanceof StatementGeneratedEvent, "Event should be StatementGeneratedEvent");
        assertEquals("statement.generated", event.type());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("STMT-TEST-FUTURE");
        periodEnd = Instant.now().plusSeconds(3600); // Future (Violation)
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate("STMT-TEST-BALANCE");
        periodEnd = Instant.now().minusSeconds(86400);
        openingBalance = new BigDecimal("50.00"); // Current opening
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalArgumentException, "Exception should be domain error");
    }

    // Additional specific step for the balance mismatch scenario setup
    @And("a previous closing balance exists that does not match")
    public void a_previous_closing_balance_exists_that_does_not_match() {
        // Handled inside the execute method hook below if we wanted to be hyper-specific,
        // but for this scenario we trigger it via the Command data passed in the When step.
    }

    // Overriding When for the specific scenario to pass the mismatching data
    @When("the GenerateStatementCmd command is executed with mismatching balance")
    public void the_generate_statement_cmd_command_is_executed_with_mismatch() {
        try {
            BigDecimal previousClosing = new BigDecimal("100.00");
            Command cmd = new GenerateStatementCmd(
                    "STMT-TEST-BALANCE",
                    accountId,
                    periodEnd,
                    openingBalance, // 50.00
                    previousClosing // 100.00
            );
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
