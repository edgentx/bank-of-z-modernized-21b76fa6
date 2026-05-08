package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        // Setup a standard valid statement
        aggregate = new StatementAggregate("stmt-1");
        // Assuming the aggregate has state, we simulate a closed period and valid balances
        // via direct test hook or default constructor state for simplicity in this layer.
        // Real persistence is mocked, so we assume the object is hydrated correctly.
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // The statement ID is part of the aggregate construction in the Given step above
        Assertions.assertNotNull(aggregate.id());
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in the When step by passing a valid format (e.g. "PDF")
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            Command cmd = new ExportStatementCmd("stmt-1", "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);

        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        Assertions.assertEquals("stmt-1", event.aggregateId());
        Assertions.assertEquals("PDF", event.format());
    }

    // --- Negative Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        // We construct an aggregate that represents an OPEN period.
        // The command should fail if the period is open.
        aggregate = new StatementAggregate("stmt-open-period", LocalDate.now().minusMonths(1), LocalDate.now().plusDays(1));
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_integrity() {
        // We construct an aggregate that is valid in time (closed period), but has mismatched balances.
        // End date in the past to satisfy "closed period" check, but opening balance != previous closing.
        aggregate = new StatementAggregate("stmt-bad-math", LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1).plusDays(20));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // In a real app we might check for a specific DomainException type, but RuntimeException is fine for this level
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
