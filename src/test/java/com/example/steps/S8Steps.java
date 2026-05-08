package com.example.steps;

import com.example.domain.shared.Command;
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
import java.time.LocalDate;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.hydrate("acct-456", LocalDate.now().minusMonths(1), LocalDate.now());
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number embedded in aggregate state for simplicity in this phase
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Period end embedded in aggregate state
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            // Retrieve state from aggregate (simplified)
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                aggregate.getId(),
                aggregate.getAccountNumber(),
                aggregate.getPeriodStart(),
                aggregate.getPeriodEnd()
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNull(caughtException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-999");
        aggregate.markAsGenerated(); // Simulate already generated/closed
        aggregate.hydrate("acct-456", LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        aggregate = new StatementAggregate("stmt-888");
        aggregate.hydrate("acct-456", LocalDate.now().minusMonths(1), LocalDate.now());
        // Inject state that would cause balance mismatch check to fail
        aggregate.setOpeningBalance(new BigDecimal("100.00"));
        // Assume logic checks against prev statement closing of 200.00
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // In DDD, domain rules are enforced via Exceptions in the execute method
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
