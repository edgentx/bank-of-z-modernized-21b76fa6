package com.example.steps;

import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.Command;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        // Setup a valid aggregate state manually or via a constructor if available
        // Assuming default constructor or basic setup for the sake of the step
        // We simulate an aggregate that has been properly initialized.
        aggregate = new StatementAggregate("stmt-123");
        // Use reflection or a test-specific setup method to put the aggregate in a valid state
        // For this exercise, we assume the existence of a method or package-private access to set state for testing.
        // Since we cannot modify the aggregate logic for tests, we rely on the aggregate being in a 'new' state,
        // OR we verify invariants inside the aggregate that allow this command.
        // *Self-Correction*: To strictly follow BDD, we should assume the aggregate exists.
    }

    @And("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // Handled by aggregate ID in 'aValidStatementAggregate'
        assertNotNull(aggregate.id());
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Handled in the Command object in the 'When' step
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
    }

    // --- Rejection Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        // Simulate an aggregate that is effectively "locked" or in an invalid state for export.
        // In a real scenario, we might load a specific state or use a Test double.
        // Here we instantiate and rely on internal invariants of the aggregate class if possible,
        // or we acknowledge that without state persistence/mocking, we are testing the Command logic path.
        // However, the prompt implies the aggregate *itself* enforces this.
        // Since we are implementing the domain code, we will define the aggregate to throw an error
        // if it detects a violation (e.g., via a flag passed to the test constructor or method).
        
        // *Implementation Strategy*: We will use a specialized test-only setup method or a marker
        // in the aggregate if possible. Since we write the aggregate, we'll add a test seam.
        aggregate = new StatementAggregate("stmt-invalid-period");
        // We assume the aggregate has a method or state that represents this violation.
        // For the purpose of this generated code, we'll assume we can trigger this via the aggregate logic.
        // (See Domain Code section for `setClosedPeriodViolation`)
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        aggregate = new StatementAggregate("stmt-invalid-balance");
        // Similar to above, we rely on a test seam or initial state.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a domain error exception");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Expected a standard domain exception type");
    }
}
