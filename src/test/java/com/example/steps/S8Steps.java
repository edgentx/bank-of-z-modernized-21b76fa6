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
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Test Data Constants
    private static final String VALID_ID = "stmt-123";
    private static final String VALID_ACCOUNT = "acc-456";
    private static final LocalDate VALID_PERIOD_END = LocalDate.of(2023, Month.OCTOBER, 31);
    private static final BigDecimal VALID_OPENING = new BigDecimal("100.00");
    private static final BigDecimal VALID_CLOSING = new BigDecimal("250.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate(VALID_ID);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in the 'When' step via constants
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Handled in the 'When' step via constants
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate(VALID_ID);
        aggregate.markPeriodAsClosed(); // Simulate the closed period state
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate(VALID_ID);
        // Simulate that the previous statement closed at 200.00, but we will attempt to open with 100.00
        aggregate.setPreviousClosingBalance(new BigDecimal("200.00"));
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            Command cmd = new GenerateStatementCmd(
                    VALID_ID,
                    VALID_ACCOUNT,
                    VALID_PERIOD_END,
                    VALID_OPENING,
                    VALID_CLOSING
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        
        DomainEvent event = resultEvents.get(0);
        Assertions.assertTrue(event instanceof StatementGeneratedEvent, "Event should be StatementGeneratedEvent");
        
        StatementGeneratedEvent stmtEvent = (StatementGeneratedEvent) event;
        Assertions.assertEquals("statement.generated", stmtEvent.type());
        Assertions.assertEquals(VALID_ID, stmtEvent.aggregateId());
        Assertions.assertEquals(VALID_ACCOUNT, stmtEvent.accountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException, "Exception should be IllegalStateException");
    }
}
