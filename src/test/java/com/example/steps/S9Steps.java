package com.example.steps;

import com.example.domain.shared.DomainException;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.domain.statement.repository.StatementRepository;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementRepository repository = new InMemoryStatementRepository();
    private StatementAggregate aggregate;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-valid-1");
        aggregate.setRepository(repository);
        aggregate.configureValidStatement();
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate("stmt-violation-period");
        aggregate.setRepository(repository);
        aggregate.configureRetroactiveViolation();
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalance() {
        aggregate = new StatementAggregate("stmt-violation-balance");
        aggregate.setRepository(repository);
        aggregate.configureBalanceMismatch();
        repository.save(aggregate);
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // ID is implied by the aggregate loaded in previous step
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Format will be provided in the command execution step
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
