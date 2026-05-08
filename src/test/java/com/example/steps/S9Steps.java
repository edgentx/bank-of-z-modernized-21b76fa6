package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-9: ExportStatementCmd
 */
public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        String statementId = "stmt-123";
        aggregate = new StatementAggregate(statementId);
        
        // Configure the aggregate to be in a valid, exportable state
        aggregate.configurePeriod("acct-456", Instant.now().minusSeconds(86400), Instant.now());
        aggregate.setBalances(BigDecimal.ZERO, BigDecimal.TEN);
        aggregate.closePeriod(); // The period must be closed to export
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        String statementId = "stmt-violation-period";
        aggregate = new StatementAggregate(statementId);
        aggregate.configurePeriod("acct-456", Instant.now(), Instant.now().plusSeconds(86400));
        aggregate.setBalances(BigDecimal.ZERO, BigDecimal.TEN);
        aggregate.markInvalidPeriod(); // Sets isPeriodClosed = false
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_opening_balance() {
        String statementId = "stmt-violation-balance";
        aggregate = new StatementAggregate(statementId);
        aggregate.configurePeriod("acct-456", Instant.now(), Instant.now().plusSeconds(86400));
        aggregate.setBalances(BigDecimal.ZERO, BigDecimal.TEN);
        aggregate.closePeriod();
        aggregate.markInvalidBalance(); // Sets openingBalance to null, triggering validation error
    }

    @Given("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // The aggregate is already initialized with an ID in the Given steps above.
        assertNotNull(aggregate.id());
    }

    @Given("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Format is handled in the When step construction
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        ExportStatementCmd cmd = new ExportStatementCmd(aggregate.id(), "PDF");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("statement.exported", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
