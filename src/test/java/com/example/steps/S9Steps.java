package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S9Steps {
    private StatementAggregate aggregate;
    private ExportStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-valid-123");
        aggregate.setClosedPeriod(true);
        aggregate.setBalanceMatched(true);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate("stmt-violate-closed-123");
        aggregate.setClosedPeriod(false); // Violation
        aggregate.setBalanceMatched(true);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesBalanceMatch() {
        aggregate = new StatementAggregate("stmt-violate-balance-123");
        aggregate.setClosedPeriod(true);
        aggregate.setBalanceMatched(false); // Violation
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // The statementId is implicitly handled by the aggregate ID, 
        // but if the command required a separate ID, we would set it here.
        // For this aggregate, the command ID must match aggregate ID or the aggregate handles it.
        // Assuming the command targets the aggregate instance.
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        this.cmd = new ExportStatementCmd(aggregate.id(), "PDF");
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        Assertions.assertEquals("statement.exported", event.type());
        Assertions.assertEquals("PDF", event.format());
        Assertions.assertTrue(event.artifactLocation().endsWith(".pdf"));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception, but none was thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
