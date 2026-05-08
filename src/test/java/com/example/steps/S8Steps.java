package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S8Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Helper to create a valid base command
    private GenerateStatementCmd createValidCommand(String id, BigDecimal prevBalance) {
        return new GenerateStatementCmd(
            id,
            "ACC-123",
            Instant.now().minusSeconds(86400 * 30), // 30 days ago
            Instant.now().minusSeconds(86400),      // Yesterday
            prevBalance,
            prevBalance.add(new BigDecimal("100.00")), // Assume some activity
            prevBalance
        );
    }

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("STMT-1");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Handled in the 'When' step via command construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            BigDecimal openingBal = new BigDecimal("1000.00");
            GenerateStatementCmd cmd = createValidCommand("STMT-1", openingBal);
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
        assertEquals("STMT-1", event.aggregateId());
        assertEquals("ACC-123", event.accountNumber());
    }

    // --- Negative Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("STMT-2");
        // Pre-generate the statement to make it immutable
        BigDecimal openingBal = new BigDecimal("1000.00");
        GenerateStatementCmd firstCmd = createValidCommand("STMT-2", openingBal);
        aggregate.execute(firstCmd);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate("STMT-3");
        // No specific state setup needed in the aggregate for this logic, 
        // the violation will be in the command execution itself.
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed_for_negative_case() {
        // Determine context based on aggregate state or specific setup
        if (aggregate.id().equals("STMT-2")) {
             // Scenario: Alter retroactively
             BigDecimal openingBal = new BigDecimal("1000.00");
             GenerateStatementCmd retryCmd = createValidCommand("STMT-2", openingBal);
             try {
                 aggregate.execute(retryCmd);
             } catch (Exception e) {
                 caughtException = e;
             }
        } else if (aggregate.id().equals("STMT-3")) {
            // Scenario: Balance mismatch
            // Previous balance was 500, but command says 600
            BigDecimal wrongOpeningBal = new BigDecimal("600.00");
            BigDecimal actualPrevClosing = new BigDecimal("500.00");
            
            GenerateStatementCmd badCmd = new GenerateStatementCmd(
                "STMT-3",
                "ACC-999",
                Instant.now().minusSeconds(86400 * 30),
                Instant.now().minusSeconds(86400),
                wrongOpeningBal,
                new BigDecimal("700.00"),
                actualPrevClosing // This is the "truth" passed to the validator
            );
            try {
                aggregate.execute(badCmd);
            } catch (Exception e) {
                caughtException = e;
            }
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "An exception should have been thrown");
        // Check for specific exception types if needed, or just presence of error
        if (aggregate.id().equals("STMT-2")) {
            assertTrue(caughtException instanceof IllegalStateException);
            assertTrue(caughtException.getMessage().contains("cannot be altered retroactively"));
        } else if (aggregate.id().equals("STMT-3")) {
            assertTrue(caughtException instanceof IllegalArgumentException);
            assertTrue(caughtException.getMessage().contains("Opening balance mismatch"));
        }
    }
}