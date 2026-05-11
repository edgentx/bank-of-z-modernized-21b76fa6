package com.example.steps;

import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private final InMemoryStatementRepository repository = new InMemoryStatementRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Standard State
    private static final String VALID_STATEMENT_ID = "stmt-123";
    private static final String VALID_FORMAT = "PDF";
    private static final BigDecimal VALID_OPENING = new BigDecimal("100.00");
    private static final BigDecimal VALID_CLOSING = new BigDecimal("200.00");

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate(VALID_STATEMENT_ID);
        // Mark as valid generated to allow the happy path
        aggregate.markAsValidGenerated(VALID_OPENING, VALID_CLOSING);
        repository.save(aggregate);
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled by constant in step definition logic
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled by constant in step definition logic
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd(VALID_STATEMENT_ID, VALID_FORMAT);
            // Reload aggregate to ensure clean state if needed, or use instance variable
            StatementAggregate aggToExecute = repository.findById(VALID_STATEMENT_ID).orElseThrow();
            resultEvents = aggToExecute.execute(cmd);
            repository.save(aggToExecute);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertFalse(resultEvents.isEmpty(), "Events list should not be empty");
        assertEquals("statement.exported", resultEvents.get(0).type());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        aggregate = new StatementAggregate(VALID_STATEMENT_ID);
        // Set the special violation behavior flag used by the aggregate logic
        aggregate.setViolationBehavior("retroactive_alteration");
        repository.save(aggregate);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        aggregate = new StatementAggregate(VALID_STATEMENT_ID);
        // Set the special violation behavior flag used by the aggregate logic
        aggregate.setViolationBehavior("opening_balance_mismatch");
        repository.save(aggregate);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException, "Expected IllegalStateException");
        assertFalse(capturedException.getMessage().isBlank(), "Error message should not be blank");
    }
}
