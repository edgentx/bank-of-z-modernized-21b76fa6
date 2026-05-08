package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S8Steps {
    private StatementAggregate aggregate;
    private String accountNumber;
    private Instant periodEnd;
    private Instant periodStart;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.accountNumber = "ACC-456";
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Valid period end must be in the past for the closed period invariant
        this.periodEnd = Instant.now().minusSeconds(3600);
        this.periodStart = this.periodEnd.minusSeconds(2592000); // 30 days prior
    }

    @Given("a valid opening balance is provided")
    public void a_valid_opening_balance_is_provided() {
        this.openingBalance = new BigDecimal("1000.00");
        this.closingBalance = new BigDecimal("1500.00");
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Ensure defaults are set for success scenario if not explicitly set by violation givens
        if (accountNumber == null) accountNumber = "ACC-456";
        if (periodEnd == null) periodEnd = Instant.now().minusSeconds(3600);
        if (periodStart == null) periodStart = periodEnd.minusSeconds(2592000);
        if (openingBalance == null) openingBalance = new BigDecimal("1000.00");
        if (closingBalance == null) closingBalance = new BigDecimal("1500.00");

        try {
            Command cmd = new GenerateStatementCmd(
                "stmt-123",
                accountNumber,
                periodStart,
                periodEnd,
                openingBalance,
                closingBalance,
                null
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("statement.generated", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on the violation, it could be IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    // Specific Given steps for violations

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-future");
        // Future period end violates the invariant
        this.periodEnd = Instant.now().plusSeconds(3600);
        this.periodStart = Instant.now();
        this.accountNumber = "ACC-456";
        this.openingBalance = new BigDecimal("100.00");
        this.closingBalance = new BigDecimal("200.00");
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-bad-bal");
        this.periodEnd = Instant.now().minusSeconds(3600);
        this.periodStart = periodEnd.minusSeconds(2592000);
        this.accountNumber = "ACC-456";
        // Null opening balance simulates the data mismatch/absence of previous check
        this.openingBalance = null;
        this.closingBalance = new BigDecimal("200.00");
    }
}