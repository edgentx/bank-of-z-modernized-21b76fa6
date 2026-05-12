package com.example.infrastructure.mongo.datasynccheckpoint;

import com.example.domain.datasynccheckpoint.model.DataSyncCheckpointAggregate;
import com.example.infrastructure.mongo.support.AggregateFieldAccess;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoDataSyncCheckpointRepositoryTest {

  private final DataSyncCheckpointMongoDataRepository data =
      mock(DataSyncCheckpointMongoDataRepository.class);
  private final MongoDataSyncCheckpointRepository repo = new MongoDataSyncCheckpointRepository(data);

  @Test
  void savePersistsIdAndVersion() {
    DataSyncCheckpointAggregate agg = new DataSyncCheckpointAggregate("dsc-1");
    AggregateFieldAccess.set(agg, "version", 9);

    repo.save(agg);
    DataSyncCheckpointDocument doc = repo.toDocument(agg);
    assertEquals("dsc-1", doc.getId());
    assertEquals(9, doc.getVersion());
    verify(data).save(any(DataSyncCheckpointDocument.class));
  }

  @Test
  void findByIdRestoresIdAndVersion() {
    when(data.findById("dsc-2")).thenReturn(Optional.of(new DataSyncCheckpointDocument("dsc-2", 12)));
    DataSyncCheckpointAggregate restored = repo.findById("dsc-2").orElseThrow();
    assertEquals("dsc-2", restored.id());
    assertEquals(12, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("nope")).thenReturn(Optional.empty());
    assertTrue(repo.findById("nope").isEmpty());
  }
}
