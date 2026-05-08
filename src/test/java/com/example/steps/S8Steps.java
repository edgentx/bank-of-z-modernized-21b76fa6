package com.example.steps;

import com.example.domain.account.model.GenerateStatementCmd;
import com.example.domain.account.model.StatementAggregate;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private String accountNumber;
    private Instant periodEnd;
    private Instant periodStart;
    private BigDecimal openingBalance;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        accountNumber = "ACC-001";
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        periodEnd = Instant.now().minusSeconds(3600); // Closed period
    }

    @And("a valid periodStart is provided")
    public void a_valid_period_start_is_provided() {
        periodStart = periodEnd.minusSeconds(86400 * 30); // 30 days prior
    }

    @And("a valid openingBalance is provided")
    public void a_valid_opening_balance_is_provided() {
        openingBalance = BigDecimal.valueOf(100.50);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                    "stmt-123",
                    accountNumber,
                    periodStart,
                    periodEnd,
                    openingBalance
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("statement.generated", resultEvents.get(0).type());
        assertNull(thrownException);
    }

    // --- Rejection Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-999");
        accountNumber = "ACC-001";
        periodStart = Instant.now().minusSeconds(86400);
        periodEnd = Instant.now().plusSeconds(3600); // Future period
        openingBalance = BigDecimal.ZERO;
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-888");
        accountNumber = "ACC-001";
        periodStart = Instant.now().minusSeconds(86400);
        periodEnd = Instant.now().minusSeconds(3600); // Valid closed period
        // Negative balance indicates mismatch/invalid state for the test logic
        openingBalance = BigDecimal.valueOf(-1.0);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertTrue(thrownException.getMessage().contains("Statement opening balance") || thrownException.getMessage().contains("closed period"));
    }
}
