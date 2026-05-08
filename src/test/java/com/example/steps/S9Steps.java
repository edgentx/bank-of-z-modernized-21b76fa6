package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.setAccountAndBalances("acct-1", new BigDecimal("100.00"), new BigDecimal("200.00"));
        aggregate.setClosed(false);
        aggregate.setGenerated(true);
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // ID handled in constructor
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format handled in command construction
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-456");
        aggregate.setAccountAndBalances("acct-1", new BigDecimal("100.00"), new BigDecimal("200.00"));
        aggregate.setClosed(true); // Violation: Period is closed
        aggregate.setGenerated(true);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate("stmt-789");
        // Violation: Opening balance is null (simulating mismatch/invalid state)
        aggregate.setAccountAndBalances("acct-1", null, new BigDecimal("200.00"));
        aggregate.setClosed(false);
        aggregate.setGenerated(true);
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("stmt-123", event.statementId());
        assertEquals("PDF", event.format());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }
}
