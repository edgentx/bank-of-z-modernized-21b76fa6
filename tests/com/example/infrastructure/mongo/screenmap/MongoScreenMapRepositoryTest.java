package com.example.infrastructure.mongo.screenmap;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoScreenMapRepositoryTest {

  private final ScreenMapMongoDataRepository data = mock(ScreenMapMongoDataRepository.class);
  private final MongoScreenMapRepository repo = new MongoScreenMapRepository(data);

  @Test
  void saveCapturesScreenMapState() {
    ScreenMapAggregate agg = new ScreenMapAggregate("sm-1");
    agg.execute(new RenderScreenCmd("sm-1", "SCRN001", "3270"));

    repo.save(agg);
    ScreenMapDocument doc = repo.toDocument(agg);
    assertEquals("sm-1", doc.getId());
    assertTrue(doc.isMandatoryFieldsValidated());
    assertTrue(doc.isBmsFieldLengthCompliant());
    assertEquals(1, doc.getVersion());
    verify(data).save(any(ScreenMapDocument.class));
  }

  @Test
  void findByIdRestoresViolationFlags() {
    ScreenMapDocument doc = new ScreenMapDocument();
    doc.setId("sm-2");
    doc.setMandatoryFieldsValidated(false);
    doc.setBmsFieldLengthCompliant(false);
    doc.setVersion(3);
    when(data.findById("sm-2")).thenReturn(Optional.of(doc));

    ScreenMapAggregate restored = repo.findById("sm-2").orElseThrow();
    assertFalse(restored.isMandatoryFieldsValidated());
    assertFalse(restored.isBmsFieldLengthCompliant());
    assertEquals(3, restored.getVersion());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("missing")).thenReturn(Optional.empty());
    assertTrue(repo.findById("missing").isEmpty());
  }
}
