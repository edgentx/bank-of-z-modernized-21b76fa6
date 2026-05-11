package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        // Logic handled in the 'When' step construction for simplicity
    }

    @Given("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Logic handled in the 'When' step construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Default valid data
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusMonths(1);
        
        if (cmd == null) {
            cmd = new GenerateStatementCmd(
                "stmt-123",
                "ACC-456",
                start,
                today.minusDays(1), // Closed period
                new BigDecimal("1000.00"),
                new BigDecimal("1500.00"),
                new BigDecimal("1000.00")  // Matches opening
            );
        }

        try {
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
    }

    // --- Scenario 2: Closed Period ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-456");
        LocalDate futureDate = LocalDate.now().plusDays(5);
        LocalDate start = LocalDate.now().plusDays(1);

        cmd = new GenerateStatementCmd(
            "stmt-456",
            "ACC-789",
            start,
            futureDate, // Future period - Invalid
            new BigDecimal("1000.00"),
            new BigDecimal("1100.00"),
            new BigDecimal("1000.00")
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
        // Check message content specific to the invariant
        assertTrue(caughtException.getMessage().contains("future") || 
                   caughtException.getMessage().contains("closed"));
    }

    // --- Scenario 3: Opening Balance Mismatch ---

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-789");
        
        cmd = new GenerateStatementCmd(
            "stmt-789",
            "ACC-101",
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusDays(1), // Valid period
            new BigDecimal("900.00"), // Opening
            new BigDecimal("1500.00"), // Closing
            new BigDecimal("1000.00")  // Previous Closing (Mismatch with 900.00)
        );
    }
}