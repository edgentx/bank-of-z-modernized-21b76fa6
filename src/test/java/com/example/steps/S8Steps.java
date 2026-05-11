package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Context setup placeholder, initialized in 'When' clause
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Context setup placeholder, initialized in 'When' clause
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-future");
        // Create a command with a future periodEnd
        cmd = new GenerateStatementCmd(
            "stmt-future",
            "acc-456",
            Instant.now().plusSeconds(3600), // Future date
            BigDecimal.ZERO,
            BigDecimal.TEN
        );
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-balance");
        // Create a command with a negative opening balance (simulating mismatch)
        cmd = new GenerateStatementCmd(
            "stmt-balance",
            "acc-789",
            Instant.now().minusSeconds(3600), // Past date
            BigDecimal.valueOf(-50.00), // Negative opening balance
            BigDecimal.TEN
        );
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            // If cmd wasn't pre-initialized by a specific 'Given' violating scenario, create a valid one
            if (cmd == null) {
                cmd = new GenerateStatementCmd(
                    "stmt-valid",
                    "acc-001",
                    Instant.now().minusSeconds(86400), // Yesterday
                    BigDecimal.valueOf(100.00),
                    BigDecimal.valueOf(150.00)
                );
            }
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals("acc-001", event.accountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
