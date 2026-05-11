package com.example.steps;

import com.example.domain.statement.model.*;
import com.example.domain.statement.repository.StatementRepository;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;

public class S9Steps {

    private StatementRepository repository = new InMemoryStatementRepository();
    private StatementAggregate aggregate;
    private Throwable thrownException;

    @Given("a valid Statement aggregate")
    public void a_valid_Statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-1");
        // Apply events to bring it to a valid state (simplified for BDD)
        // In a real scenario, we might load this from the repo.
    }

    @Given("a valid statementId is provided")
    public void a_valid_statementId_is_provided() {
        // Implicit in the aggregate ID
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Parameter for the command
    }

    @When("the ExportStatementCmd command is executed")
    public void the_ExportStatementCmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd("stmt-1", "PDF");
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        Assertions.assertTrue(aggregate.uncommittedEvents().get(0) instanceof StatementExportedEvent);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_Statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-invalid-period");
        // Simulate the aggregate being in a state where export is invalid due to period closure rules
        // For the purpose of this test, we flag the aggregate internally or rely on specific setup logic
        aggregate.markPeriodOpen(); 
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_Statement_aggregate_that_violates_balance_matching() {
        this.aggregate = new StatementAggregate("stmt-invalid-balance");
        // Simulate a mismatch
        aggregate.markBalanceMismatch();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Should have thrown an exception");
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Exception should be a domain error"
        );
    }
    
    // Inner class for mocking the repository if needed by the pattern
    static class InMemoryStatementRepository implements StatementRepository {
        @Override
        public Optional<StatementAggregate> findById(String id) {
            return Optional.empty();
        }
        @Override
        public void save(StatementAggregate aggregate) { }
    }
}
