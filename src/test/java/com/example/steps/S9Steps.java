package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.domain.statement.repository.StatementRepository;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private final StatementRepository repository = new InMemoryStatementRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-1", "acc-1", BigDecimal.ZERO, BigDecimal.ZERO);
        aggregate.markAsGeneratedAndClosed();
        repository.save(aggregate);
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled in aggregate construction
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in command execution
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate("stmt-2", "acc-1", BigDecimal.ZERO, BigDecimal.ZERO);
        // Intentionally not marking as generated/closed
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_matching() {
        aggregate = new StatementAggregate("stmt-3", "acc-1", new BigDecimal("100.00"), new BigDecimal("99.99"));
        aggregate.markAsGeneratedAndClosed();
        repository.save(aggregate);
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }
}