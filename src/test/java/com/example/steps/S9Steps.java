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
        aggregate = new StatementAggregate("stmt-123", new BigDecimal("100.00"));
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Id is hardcoded in the aggregate creation for this test
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format will be provided in the command execution
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_period_not_closed() {
        aggregate = new StatementAggregate("stmt-999", BigDecimal.ZERO);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_balance_mismatch() {
        aggregate = new StatementAggregate("stmt-888", new BigDecimal("500.00"));
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        // Determine parameters based on context (simulating different scenarios)
        // In a real test, we might use scenario context, but here we infer from the aggregate state or defaults.
        
        boolean isClosed = true;
        BigDecimal previousClosing = aggregate.getOpeningBalance(); // Default matches
        
        // Adjust for specific failure scenarios based on the Aggregate ID used in Given
        if (aggregate.id().equals("stmt-999")) {
            isClosed = false; // Violate closed period
        } else if (aggregate.id().equals("stmt-888")) {
            previousClosing = new BigDecimal("0.00"); // Violate balance match (aggregate is 500)
        }

        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF", previousClosing, isClosed);
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
        assertEquals("statement.exported", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("PDF", event.format());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}