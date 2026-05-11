package com.example.steps;

import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S9Steps {

    private StatementAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-valid");
        // Apply an initial event to simulate a valid state (e.g. opened)
        aggregate.apply(new StatementExportedEvent(
            "stmt-valid", 
            "acct-1", 
            Instant.now(), 
            Instant.now().plusSeconds(86400), 
            BigDecimal.ZERO, 
            BigDecimal.ZERO, 
            true, // isClosed
            false // balance matches
        ));
        aggregate.clearEvents();
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-closed-violation");
        // Simulate a state that cannot be altered
        aggregate.apply(new StatementExportedEvent(
            "stmt-closed-violation", 
            "acct-1", 
            Instant.now().minus(90, java.time.temporal.ChronoUnit.DAYS), // old date
            Instant.now().minus(60, java.time.temporal.ChronoUnit.DAYS), 
            BigDecimal.ZERO, 
            BigDecimal.ZERO, 
            true, 
            false
        ));
        aggregate.markAsArchived(); // Hypothetical method to enforce invariants
        aggregate.clearEvents();
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_mismatch() {
        aggregate = new StatementAggregate("stmt-balance-mismatch");
        aggregate.apply(new StatementExportedEvent(
            "stmt-balance-mismatch", 
            "acct-1", 
            Instant.now(), 
            Instant.now().plusSeconds(86400), 
            BigDecimal.valueOf(100.00), // Opening
            BigDecimal.valueOf(500.00), // Closing
            true, 
            true // This flag 'true' here means balance mismatched in constructor logic
        ));
        aggregate.clearEvents();
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // ID is part of the aggregate construction, implicit in the steps above
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format will be provided in the command execution
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd("stmt-id", "PDF");
        // Adjust ID for specific scenarios if necessary, usually matching the aggregate ID
        if (aggregate.id().equals("stmt-valid")) {
            cmd = new ExportStatementCmd("stmt-valid", "PDF");
        } else if (aggregate.id().equals("stmt-closed-violation")) {
            cmd = new ExportStatementCmd("stmt-closed-violation", "PDF");
        } else if (aggregate.id().equals("stmt-balance-mismatch")) {
            cmd = new ExportStatementCmd("stmt-balance-mismatch", "PDF");
        }

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("StatementExportedEvent", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // We check for IllegalStateException or IllegalArgumentException as domain errors
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
