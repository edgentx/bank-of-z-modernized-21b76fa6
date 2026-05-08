package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
public class S9Steps {

    private StatementAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private String statementId;
    private String format;

    @Given("a valid Statement aggregate")
    public void a_valid_statement_aggregate() {
        this.statementId = UUID.randomUUID().toString();
        this.aggregate = new StatementAggregate(this.statementId);
        
        // Simulating a valid state via reflection or setter logic for the test context 
        // In a real app, we might replay events, but here we set state to pass validation.
        aggregate.setOpeningBalance(new BigDecimal("100.00"));
        aggregate.setPreviousClosingBalance(new BigDecimal("100.00"));
        aggregate.setPeriodClosed(true);
    }

    @And("a valid statementId is provided")
    public void a_valid_statement_id_is_provided() {
        assertNotNull(this.statementId);
    }

    @And("a valid format is provided")
    public void a_valid_format_is_provided() {
        this.format = "PDF";
    }

    @When("the ExportStatementCmd command is executed")
    public void the_export_statement_cmd_command_is_executed() {
        try {
            Command cmd = new ExportStatementCmd(this.statementId, this.format);
            this.resultEvents = this.aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void a_statement_exported_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent);
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals(this.statementId, event.aggregateId());
        assertEquals("PDF", event.format());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void a_statement_aggregate_that_violates_closed_period() {
        this.statementId = UUID.randomUUID().toString();
        this.aggregate = new StatementAggregate(this.statementId);
        this.format = "PDF";
        
        // Set state to violation: Period is NOT closed
        aggregate.setPeriodClosed(false);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void a_statement_aggregate_that_violates_balance_match() {
        this.statementId = UUID.randomUUID().toString();
        this.aggregate = new StatementAggregate(this.statementId);
        this.format = "PDF";
        
        aggregate.setPeriodClosed(true); // Satisfy closed period
        aggregate.setOpeningBalance(new BigDecimal("100.00"));
        aggregate.setPreviousClosingBalance(new BigDecimal("200.00")); // Mismatch
    }
}
