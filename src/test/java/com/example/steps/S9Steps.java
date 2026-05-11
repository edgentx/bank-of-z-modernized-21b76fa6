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

public class S9Steps {

    private StatementAggregate aggregate;
    private ExportStatementCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        aggregate = new StatementAggregate("stmt-123");
        aggregate.markPeriodClosedAndGenerated("100.00");
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_period_open() {
        aggregate = new StatementAggregate("stmt-456");
        // Leaving period open (periodClosed = false)
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_balance_mismatch() {
        aggregate = new StatementAggregate("stmt-789");
        aggregate.markPeriodClosedAndGenerated("100.00"); // Current opens at 100
        // Scenario implies previous closed at something else, e.g. 99.00
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        // Handled in cmd construction
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        // Handled in cmd construction
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            // Construct cmd with context for balance matching check if needed
            // For the mismatch scenario, we simulate the check in the execute method via cmd args
            
            String prevClose = "100.00"; // Assume previous closed at 100
            String currOpen = "100.00";
            
            if (aggregate.id().equals("stmt-789")) {
                // Force mismatch condition for test
                prevClose = "99.00";
                currOpen = "100.00";
            }
            
            cmd = new ExportStatementCmd(aggregate.id(), "PDF", aggregate.id() + "-prev", prevClose, currOpen);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        Assertions.assertEquals("statement.exported", event.type());
        Assertions.assertEquals("PDF", event.format());
        Assertions.assertTrue(event.documentLocation().startsWith("minio://documents/statements/"));
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}
