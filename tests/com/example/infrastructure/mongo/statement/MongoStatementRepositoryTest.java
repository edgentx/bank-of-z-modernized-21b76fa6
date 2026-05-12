package com.example.infrastructure.mongo.statement;

import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoStatementRepositoryTest {

  private final StatementMongoDataRepository data = mock(StatementMongoDataRepository.class);
  private final MongoStatementRepository repo = new MongoStatementRepository(data);

  @Test
  void saveCapturesGeneratedAndExportedState() {
    StatementAggregate agg = new StatementAggregate("stmt-1");
    agg.execute(new GenerateStatementCmd("ACC-1", LocalDate.of(2026, 4, 30)));
    agg.execute(new ExportStatementCmd("stmt-1", "PDF"));

    repo.save(agg);
    StatementDocument doc = repo.toDocument(agg);
    assertEquals("stmt-1", doc.getId());
    assertEquals("ACC-1", doc.getAccountNumber());
    assertTrue(doc.isGenerated());
    assertTrue(doc.isExported());
    assertEquals("PDF", doc.getExportedFormat());
    assertEquals(2, doc.getVersion());
    verify(data).save(any(StatementDocument.class));
  }

  @Test
  void findByIdRestoresState() {
    StatementDocument doc = new StatementDocument();
    doc.setId("stmt-2");
    doc.setAccountNumber("ACC-2");
    doc.setGenerated(true);
    doc.setExported(false);
    doc.setExportedFormat(null);
    doc.setVersion(1);
    when(data.findById("stmt-2")).thenReturn(Optional.of(doc));

    StatementAggregate restored = repo.findById("stmt-2").orElseThrow();
    assertEquals("ACC-2", restored.getAccountNumber());
    assertTrue(restored.isGenerated());
    assertFalse(restored.isExported());
    assertEquals(1, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("none")).thenReturn(Optional.empty());
    assertTrue(repo.findById("none").isEmpty());
  }
}
