package com.example.steps;

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

/**
 * Cucumber Steps for S-8: Implement GenerateStatementCmd on Statement.
 */
public class S8Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper data for valid context
    private static final String VALID_ACCOUNT = "ACC-12345";
    private static final Instant VALID_START = Instant.now().minus(30, ChronoUnit.DAYS);
    private static final Instant VALID_END = Instant.now();
    private static final BigDecimal VALID_OPENING = BigDecimal.valueOf(1000.00);
    private static final BigDecimal VALID_CLOSING = BigDecimal.valueOf(1500.00);

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-test-1");
        // Defaults: not closed, no previous balance constraints interfering
        aggregate.setPreviousStatementClosingBalance(null);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Parameter data is prepared in the When step for simplicity in this pattern,
        // but we ensure the aggregate is ready.
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Parameter data prepared in When step.
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
            aggregate.id(),
            VALID_ACCOUNT,
            VALID_START,
            VALID_END,
            VALID_OPENING,
            VALID_CLOSING
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Exactly one event should be emitted");
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent, "Event should be StatementGeneratedEvent");
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals(VALID_ACCOUNT, event.accountNumber());
    }

    // --- Error Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period_constraint() {
        aggregate = new StatementAggregate("stmt-test-closed");
        aggregate.markAsClosed(); // Simulate the invariant violation state
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance_constraint() {
        aggregate = new StatementAggregate("stmt-test-balance-mismatch");
        // Simulate a previous statement with a closing balance of 2000
        aggregate.setPreviousStatementClosingBalance(BigDecimal.valueOf(2000.00));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)");
    }
}
