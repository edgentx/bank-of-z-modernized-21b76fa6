package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S9Steps {

    private StatementAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        this.aggregate = new StatementAggregate("stmt-123");
        // Configure it to be valid (closed period, valid balances)
        aggregate.configureAsValid(
            "acct-456", 
            Instant.now().minusSeconds(86400), 
            Instant.now(), 
            new BigDecimal("100.00"), 
            new BigDecimal("200.00")
        );
    }

    @Given("a valid statementId is provided")
    public void aValidStatementIdIsProvided() {
        // The ID is implicitly provided via the aggregate ID or command construction in later steps
        // No-op for this step, structure provided by the aggregate initialization
    }

    @And("a valid format is provided")
    public void aValidFormatIsProvided() {
        // Format is provided in the When step
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            ExportStatementCmd cmd = new ExportStatementCmd("stmt-123", "PDF");
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should have emitted one event");
        assertTrue(resultEvents.get(0) instanceof StatementExportedEvent, "Event should be StatementExportedEvent");
        
        StatementExportedEvent event = (StatementExportedEvent) resultEvents.get(0);
        assertEquals("stmt-123", event.aggregateId());
        assertEquals("PDF", event.format());
        assertTrue(event.artifactLocation().endsWith(".pdf"));
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        this.aggregate = new StatementAggregate("stmt-open-123");
        // Do not close the period
        aggregate.markPeriodOpen(); 
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Should have thrown an exception");
        assertTrue(capturedException instanceof IllegalStateException, "Should be an IllegalStateException");
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        this.aggregate = new StatementAggregate("stmt-corrupt-123");
        // Configure as valid but then corrupt the balance to simulate the violation check failure
        aggregate.configureAsValid("acct-1", Instant.now(), Instant.now(), BigDecimal.ZERO, BigDecimal.ZERO);
        aggregate.corruptOpeningBalance();
    }
}
