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
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private String accountNumber;
    private Instant periodEnd;
    private BigDecimal openingBalance;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario: Successfully execute GenerateStatementCmd
    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        String id = "stmt-" + System.currentTimeMillis();
        aggregate = new StatementAggregate(id);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.accountNumber = "ACC-12345";
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        this.periodEnd = Instant.now().truncatedTo(ChronoUnit.DAYS);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default opening balance matching the 'previous closing' (assumed 100.00 in aggregate)
        this.openingBalance = new BigDecimal("100.00");
        Command cmd = new GenerateStatementCmd(aggregate.id(), accountNumber, periodEnd, openingBalance);
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

        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("statement.generated", event.type());
    }

    // Scenario: GenerateStatementCmd rejected — Retroactive
    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        String id = "stmt-retro-" + System.currentTimeMillis();
        aggregate = new StatementAggregate(id);
        // Force the internal state that simulates a closed period
        aggregate.markPeriodAsClosed();
        this.accountNumber = "ACC-99999";
        this.periodEnd = Instant.now().minusSeconds(86400); // Yesterday
        this.openingBalance = new BigDecimal("100.00");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("closed") || caughtException.getMessage().contains("retroactive"));
    }

    // Scenario: GenerateStatementCmd rejected — Opening Balance Mismatch
    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        String id = "stmt-bal-" + System.currentTimeMillis();
        aggregate = new StatementAggregate(id);
        this.accountNumber = "ACC-88888";
        this.periodEnd = Instant.now();
        // Aggregate expects 100.00 (simulated previous closing), we provide 99.00
        this.openingBalance = new BigDecimal("99.00");
    }

}
