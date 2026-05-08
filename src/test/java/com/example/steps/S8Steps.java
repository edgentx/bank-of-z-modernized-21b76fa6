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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-8: Implement GenerateStatementCmd on Statement.
 */
public class S8Steps {

    private StatementAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test Data Constants
    private static final String TEST_STATEMENT_ID = "stmt-123";
    private static final String TEST_ACCOUNT_NUMBER = "acc-456";
    private static final LocalDate VALID_PERIOD_END = LocalDate.of(2023, Month.OCTOBER, 31);
    private static final BigDecimal OPENING_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal CLOSING_BALANCE = new BigDecimal("1500.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        // Simulating a clean state where the statement hasn't been generated yet
        aggregate.setGenerated(false);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Context setup handled in the 'When' step construction
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Context setup handled in the 'When' step construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            TEST_STATEMENT_ID,
            TEST_ACCOUNT_NUMBER,
            VALID_PERIOD_END,
            OPENING_BALANCE,
            CLOSING_BALANCE
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents, "Result events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof StatementGeneratedEvent, "Event should be StatementGeneratedEvent");
        
        StatementGeneratedEvent stmtEvent = (StatementGeneratedEvent) event;
        assertEquals("statement.generated", stmtEvent.type());
        assertEquals(TEST_STATEMENT_ID, stmtEvent.statementId());
        assertEquals(TEST_ACCOUNT_NUMBER, stmtEvent.accountNumber());
    }

    // --- Negative Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period_constraint() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        // Simulating that the statement is already generated (closed)
        aggregate.setGenerated(true);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance_constraint() {
        aggregate = new StatementAggregate(TEST_STATEMENT_ID);
        aggregate.setGenerated(false);
        // Simulate a previous closing balance of 2000.00
        BigDecimal previousClosing = new BigDecimal("2000.00");
        aggregate.setPreviousClosingBalance(previousClosing);
        // The command will send 1000.00, violating the constraint
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We expect either an IllegalStateException (for closed period) or IllegalArgumentException (for balance mismatch)
        assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected a domain error exception, but got: " + capturedException.getClass().getSimpleName()
        );
    }
}
