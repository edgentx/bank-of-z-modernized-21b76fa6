package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import com.example.domain.statement.repository.StatementRepository;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class S8Steps {

    private StatementAggregate aggregate;
    private StatementRepository repository = new InMemoryStatementRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.markAsGenerated(); // Simulating it is already generated/closed
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.setOpeningBalance(new BigDecimal("100.00"));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is provided in the command construction, handled in 'When'
    }

    @And("a valid periodEnd is provided")
    public void a_valid_period_end_is_provided() {
        // Period end is provided in the command construction, handled in 'When'
    }

    @When("the GenerateStatementCmd command is executed")
    public void the_generate_statement_cmd_command_is_executed() {
        try {
            var cmd = new GenerateStatementCmd(
                "stmt-123",
                "ACC-456",
                LocalDate.now().minusMonths(1),
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                null // No previous closing balance in happy path
            );
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @When("the GenerateStatementCmd command is executed with mismatched opening balance")
    public void the_generate_statement_cmd_command_is_executed_with_mismatch() {
        try {
            var cmd = new GenerateStatementCmd(
                "stmt-123",
                "ACC-456",
                LocalDate.now().minusMonths(1),
                new BigDecimal("500.00"), // Opening balance in cmd differs from aggregate state
                new BigDecimal("600.00"),
                new BigDecimal("100.00")  // Previous closing balance
            );
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void a_statement_generated_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Check for specific error messages or types if necessary
    }
}
