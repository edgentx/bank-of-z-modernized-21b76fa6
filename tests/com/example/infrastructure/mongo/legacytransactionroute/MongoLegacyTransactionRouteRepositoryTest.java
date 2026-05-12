package com.example.infrastructure.mongo.legacytransactionroute;

import com.example.domain.legacytransactionroute.model.LegacyTransactionRouteAggregate;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoLegacyTransactionRouteRepositoryTest {

  private final LegacyTransactionRouteMongoDataRepository data =
      mock(LegacyTransactionRouteMongoDataRepository.class);
  private final MongoLegacyTransactionRouteRepository repo =
      new MongoLegacyTransactionRouteRepository(data);

  @Test
  void savePersistsIdAndVersion() {
    LegacyTransactionRouteAggregate agg = new LegacyTransactionRouteAggregate("ltr-1");
    AggregateFieldAccess.set(agg, "version", 1);

    repo.save(agg);
    LegacyTransactionRouteDocument doc = repo.toDocument(agg);
    assertEquals("ltr-1", doc.getId());
    assertEquals(1, doc.getVersion());
    verify(data).save(any(LegacyTransactionRouteDocument.class));
  }

  @Test
  void findByIdRestoresIdAndVersion() {
    when(data.findById("ltr-2")).thenReturn(Optional.of(new LegacyTransactionRouteDocument("ltr-2", 4)));
    LegacyTransactionRouteAggregate restored = repo.findById("ltr-2").orElseThrow();
    assertEquals("ltr-2", restored.id());
    assertEquals(4, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("missing")).thenReturn(Optional.empty());
    assertTrue(repo.findById("missing").isEmpty());
  }
}
