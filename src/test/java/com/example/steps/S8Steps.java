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
    private String statementId = "stmt-123";
    private String accountNumber = "acc-456";
    private Instant periodStart;
    private Instant periodEnd;
    private BigDecimal openingBalance = BigDecimal.ZERO;
    private BigDecimal closingBalance = new BigDecimal("100.00");
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate(statementId);
        // Assume previous balance was 0 for a valid happy path
        aggregate.setPreviousPeriodClosingBalance(BigDecimal.ZERO);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate(statementId);
        // Set up state such that the command will use a future date
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate(statementId);
        // Simulate a scenario where the previous statement closed at 100.00
        aggregate.setPreviousPeriodClosingBalance(new BigDecimal("100.00"));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // accountNumber is defaulted
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        periodEnd = Instant.now().minus(1, ChronoUnit.DAYS);
        periodStart = periodEnd.minus(30, ChronoUnit.DAYS);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            Command cmd = new GenerateStatementCmd(
                statementId,
                accountNumber,
                periodStart,
                periodEnd,
                openingBalance,
                closingBalance
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
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals(statementId, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

}