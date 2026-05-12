package com.example.infrastructure.mongo.reconciliationbatch;

import com.example.domain.reconciliationbatch.model.ReconciliationBatchAggregate;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoReconciliationBatchRepositoryTest {

  private final ReconciliationBatchMongoDataRepository data =
      mock(ReconciliationBatchMongoDataRepository.class);
  private final MongoReconciliationBatchRepository repo = new MongoReconciliationBatchRepository(data);

  @Test
  void savePersistsIdAndVersion() {
    ReconciliationBatchAggregate agg = new ReconciliationBatchAggregate("rb-1");
    AggregateFieldAccess.set(agg, "version", 2);

    repo.save(agg);
    ReconciliationBatchDocument doc = repo.toDocument(agg);
    assertEquals("rb-1", doc.getId());
    assertEquals(2, doc.getVersion());
    verify(data).save(any(ReconciliationBatchDocument.class));
  }

  @Test
  void findByIdRestoresIdAndVersion() {
    ReconciliationBatchDocument doc = new ReconciliationBatchDocument("rb-2", 5);
    when(data.findById("rb-2")).thenReturn(Optional.of(doc));

    ReconciliationBatchAggregate restored = repo.findById("rb-2").orElseThrow();
    assertEquals("rb-2", restored.id());
    assertEquals(5, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("none")).thenReturn(Optional.empty());
    assertTrue(repo.findById("none").isEmpty());
  }
}
