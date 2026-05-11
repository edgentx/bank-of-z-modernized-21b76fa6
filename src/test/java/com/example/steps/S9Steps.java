package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private final InMemoryStatementRepository repository = new InMemoryStatementRepository();
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-123");
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateInClosedPeriod() {
        aggregate = new StatementAggregate("stmt-456");
        aggregate.markClosedPeriod(); // Force violation
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateWithMismatchedBalance() {
        aggregate = new StatementAggregate("stmt-789");
        aggregate.markOpeningBalanceInvalid(); // Force violation
        repository.save(aggregate);
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // ID is provided via aggregate construction in Given steps
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Format will be provided in the Command execution
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("statement.exported", event.type());
        assertEquals("PDF", event.format());
        assertTrue(aggregate.isExported());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
        assertFalse(aggregate.isExported());
    }
}
