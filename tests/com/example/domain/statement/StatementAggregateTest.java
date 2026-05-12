package com.example.domain.statement;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import com.example.mocks.InMemoryStatementRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatementAggregateTest {

  @Test void exportHappyPathEmitsStatementExportedEvent() {
    var stmt = new StatementAggregate("stmt-1");
    List<DomainEvent> events = stmt.execute(new ExportStatementCmd("stmt-1", "PDF"));
    assertEquals(1, events.size());
    var e = (StatementExportedEvent) events.get(0);
    assertEquals("statement.exported", e.type());
    assertEquals("PDF", e.format());
    assertTrue(stmt.isExported());
    assertEquals("PDF", stmt.getExportedFormat());
    assertEquals(1, stmt.getVersion());
  }

  @Test void exportRejectedWhenClosedPeriodViolated() {
    var stmt = new StatementAggregate("stmt-open-period");
    stmt.markClosedPeriodViolation();
    assertThrows(IllegalStateException.class,
        () -> stmt.execute(new ExportStatementCmd("stmt-open-period", "PDF")));
  }

  @Test void exportRejectedWhenOpeningBalanceViolated() {
    var stmt = new StatementAggregate("stmt-bad-opening");
    stmt.markOpeningBalanceViolation();
    assertThrows(IllegalStateException.class,
        () -> stmt.execute(new ExportStatementCmd("stmt-bad-opening", "PDF")));
  }

  @Test void exportRejectsBlankFormat() {
    var stmt = new StatementAggregate("stmt-2");
    assertThrows(IllegalArgumentException.class,
        () -> stmt.execute(new ExportStatementCmd("stmt-2", "  ")));
  }

  @Test void exportRejectsBlankStatementId() {
    var stmt = new StatementAggregate("stmt-3");
    assertThrows(IllegalArgumentException.class,
        () -> stmt.execute(new ExportStatementCmd("", "PDF")));
  }

  @Test void exportPersistsThroughRepository() {
    var repo = new InMemoryStatementRepository();
    var stmt = new StatementAggregate("stmt-4");
    stmt.execute(new ExportStatementCmd("stmt-4", "CSV"));
    repo.save(stmt);
    var loaded = repo.findById("stmt-4").orElseThrow();
    assertTrue(loaded.isExported());
    assertEquals("CSV", loaded.getExportedFormat());
  }
}
