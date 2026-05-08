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
import java.time.Instant;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private GenerateStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Test Constants
    private static final String TEST_STMT_ID = "stmt-123";
    private static final String TEST_ACCOUNT = "acc-456";
    private static final Instant NOW = Instant.now();
    private static final BigDecimal HUNDRED = new BigDecimal("100.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate(TEST_STMT_ID);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in context of command construction
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Handled in context of command construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        // Construct a valid command by default if not already set by a specific violation scenario
        if (this.cmd == null) {
            this.cmd = new GenerateStatementCmd(
                TEST_STMT_ID,
                TEST_ACCOUNT,
                NOW.minusSeconds(86400),
                NOW,
                HUNDRED,
                HUNDRED.add(BigDecimal.TEN),
                HUNDRED
            );
        }

        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);

        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        Assertions.assertEquals(TEST_ACCOUNT, event.accountNumber());
        Assertions.assertEquals("statement.generated", event.type());
    }

    // --- Scenario: Closed Period Violation ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate(TEST_STMT_ID);
        // We simulate the 'Closed Period' by pre-generating the statement (idempotency check)
        // Executing a valid command first puts the aggregate in the 'closed' state.
        GenerateStatementCmd initialCmd = new GenerateStatementCmd(
                TEST_STMT_ID,
                TEST_ACCOUNT,
                NOW.minusSeconds(86400),
                NOW,
                HUNDRED,
                HUNDRED.add(BigDecimal.TEN),
                HUNDRED
        );
        // Execute internally to set state
        aggregate.execute(initialCmd);
        
        // Now prepare the command that the test will attempt to run (the 'When' step)
        this.cmd = initialCmd; // Re-running the same command triggers the "already generated" error
    }

    // --- Scenario: Opening Balance Mismatch ---

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_mismatch() {
        this.aggregate = new StatementAggregate(TEST_STMT_ID);
        
        // Prepare a command where Opening (50) != Previous Closing (100)
        this.cmd = new GenerateStatementCmd(
                TEST_STMT_ID,
                TEST_ACCOUNT,
                NOW.minusSeconds(86400),
                NOW,
                new BigDecimal("50.00"),  // Opening
                HUNDRED.add(BigDecimal.TEN),
                HUNDRED // Previous Closing
        );
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException)"
        );
    }
}
