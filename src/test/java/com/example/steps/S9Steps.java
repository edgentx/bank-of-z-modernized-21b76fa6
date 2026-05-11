package com.example.steps;

import com.example.domain.statement.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.En;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.aggregate = new StatementAggregate("stmt-1");
        // Apply a creation event to bring it to a valid state for testing commands
        // In a real repo flow we'd load, but here we manually hydrate or use a creation cmd
        // Assuming the aggregate starts in a valid, closed state for export
        aggregate.applyStateChange(new StatementGeneratedEvent("stmt-1", "acct-1", BigDecimal.ZERO, BigDecimal.TEN, false, java.time.Instant.now()));
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by the aggregate construction in the previous step
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled by the command construction in the When step
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        Command cmd = new ExportStatementCmd("stmt-1", "PDF");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        Assertions.assertEquals("stmt-1", event.statementId());
        Assertions.assertEquals("PDF", event.format());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.aggregate = new StatementAggregate("stmt-2");
        // Set state to OPEN, which violates the invariant for export
        aggregate.applyStateChange(new StatementGeneratedEvent("stmt-2", "acct-1", BigDecimal.ZERO, BigDecimal.TEN, true, java.time.Instant.now()));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        // Or specific DomainException if defined, using IllegalStateException as per examples
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_reconciliation() {
        this.aggregate = new StatementAggregate("stmt-3");
        // We can't easily set the "Previous Closing Balance" without a repository or complex state
        // However, we can simulate the scenario by assuming the aggregate has knowledge of a mismatch.
        // For this BDD step, we will create the aggregate in a state where it knows the previous balance was 100, but it opened with 0.
        // Since the aggregate in this simplified context doesn't hold the 'previous' balance, we will simulate the check by passing a specific command or relying on internal state.
        // Let's assume the aggregate stores the expected previous closing balance internally for validation purposes.
        aggregate = new StatementAggregate("stmt-3", BigDecimal.valueOf(100)); // Expecting 100
        aggregate.applyStateChange(new StatementGeneratedEvent("stmt-3", "acct-1", BigDecimal.ZERO, BigDecimal.TEN, false, java.time.Instant.now()));
        // Current opening balance is 0. Expected (via constructor) is 100. Mismatch.
    }
}
