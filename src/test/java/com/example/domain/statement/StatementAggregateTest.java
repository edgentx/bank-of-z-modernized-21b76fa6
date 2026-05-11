package com.example.domain.statement;

import com.example.domain.shared.UnknownCommandException;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Red Phase Tests for Statement Aggregate.
 * These tests are expected to fail until the implementation logic in StatementAggregate is completed
 * (or in this case, verify the logic we implemented in the previous step).
 */
class StatementAggregateTest {

    private static final String STATEMENT_ID = "stmt-123";
    private static final String ACCOUNT_ID = "acct-456";
    private static final BigDecimal OPENING_BALANCE = new BigDecimal("100.00");
    private static final BigDecimal CLOSING_BALANCE = new BigDecimal("200.00");

    @Test
    void scenario_successfully_execute_export_statement_cmd() {
        // Given
        StatementAggregate aggregate = new StatementAggregate(STATEMENT_ID);
        aggregate.setAccountAndDates(ACCOUNT_ID, Instant.now().minusSeconds(86400), Instant.now(), true);
        aggregate.setBalances(OPENING_BALANCE, CLOSING_BALANCE);

        ExportStatementCmd cmd = new ExportStatementCmd(STATEMENT_ID, "PDF");

        // When
        List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);

        // Then
        assertFalse(events.isEmpty(), "Should emit an event");
        assertTrue(events.get(0) instanceof StatementExportedEvent, "Should be StatementExportedEvent");

        StatementExportedEvent exportedEvent = (StatementExportedEvent) events.get(0);
        assertEquals("statement.exported", exportedEvent.type());
        assertEquals(STATEMENT_ID, exportedEvent.statementId());
        assertEquals("PDF", exportedEvent.format());
        assertNotNull(exportedEvent.artifactLocation());
    }

    @Test
    void scenario_rejected_when_period_not_closed() {
        // Given: Statement violates "A statement must be generated for a closed period"
        StatementAggregate aggregate = new StatementAggregate(STATEMENT_ID);
        aggregate.setAccountAndDates(ACCOUNT_ID, Instant.now().minusSeconds(86400), Instant.now(), false); // isClosed = false
        aggregate.setBalances(OPENING_BALANCE, CLOSING_BALANCE);

        ExportStatementCmd cmd = new ExportStatementCmd(STATEMENT_ID, "PDF");

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Period is not closed"));
    }

    @Test
    void scenario_rejected_when_opening_balance_invalid() {
        // Given: Statement violates "Statement opening balance must exactly match the closing balance of the previous statement"
        StatementAggregate aggregate = new StatementAggregate(STATEMENT_ID);
        aggregate.setAccountAndDates(ACCOUNT_ID, Instant.now().minusSeconds(86400), Instant.now(), true);
        aggregate.setBalances(null, CLOSING_BALANCE); // Opening balance null simulates validation failure state

        ExportStatementCmd cmd = new ExportStatementCmd(STATEMENT_ID, "PDF");

        // When & Then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            aggregate.execute(cmd);
        });

        assertTrue(ex.getMessage().contains("Opening balance does not match"));
    }

    @Test
    void handle_unknown_command_throws_exception() {
        StatementAggregate aggregate = new StatementAggregate(STATEMENT_ID);
        aggregate.setAccountAndDates(ACCOUNT_ID, Instant.now().minusSeconds(86400), Instant.now(), true);
        aggregate.setBalances(OPENING_BALANCE, CLOSING_BALANCE);

        // Unknown command type (simulated with a lambda or explicit new command class if we had one)
        Command unknownCmd = new Command() {};

        assertThrows(UnknownCommandException.class, () -> aggregate.execute(unknownCmd));
    }
}
