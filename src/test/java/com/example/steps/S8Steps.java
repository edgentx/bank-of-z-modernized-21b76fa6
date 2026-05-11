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

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    private final String STATEMENT_ID = "stmt-123";
    private final String ACCOUNT_NUMBER = "acct-456";
    private final Instant PERIOD_END = Instant.now();
    private final BigDecimal VALID_OPENING = new BigDecimal("100.00");
    private final BigDecimal VALID_PREVIOUS_CLOSING = new BigDecimal("100.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate(STATEMENT_ID);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Value stored in context for use in When
        // Implicitly handled by using constants in context
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Value stored in context for use in When
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        GenerateStatementCmd cmd = new GenerateStatementCmd(
                STATEMENT_ID,
                ACCOUNT_NUMBER,
                PERIOD_END,
                VALID_OPENING,
                VALID_PREVIOUS_CLOSING,
                Instant.now()
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNull(caughtException, "Expected no error, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals(ACCOUNT_NUMBER, event.accountNumber());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate(STATEMENT_ID);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed_for_closed_period() {
        // Simulate retroactive request by setting 'generatedAt' far in the past
        GenerateStatementCmd cmd = new GenerateStatementCmd(
                STATEMENT_ID,
                ACCOUNT_NUMBER,
                PERIOD_END,
                VALID_OPENING,
                VALID_PREVIOUS_CLOSING,
                Instant.now().minusSeconds(120) // > 60 seconds ago
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
        assertTrue(caughtException.getMessage().contains("closed period")
                || caughtException.getMessage().contains("closing balance"));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate(STATEMENT_ID);
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed_for_balance_mismatch() {
        // Opening balance does not match previous closing
        GenerateStatementCmd cmd = new GenerateStatementCmd(
                STATEMENT_ID,
                ACCOUNT_NUMBER,
                PERIOD_END,
                new BigDecimal("50.00"), // Mismatch
                new BigDecimal("100.00"), // Previous close
                Instant.now()
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
