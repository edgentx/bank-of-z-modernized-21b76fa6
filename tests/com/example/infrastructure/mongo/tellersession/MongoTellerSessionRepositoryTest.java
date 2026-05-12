package com.example.infrastructure.mongo.tellersession;

import com.example.domain.tellersession.model.StartSessionCmd;
import com.example.domain.tellersession.model.TellerSessionAggregate;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoTellerSessionRepositoryTest {

  private final TellerSessionMongoDataRepository data = mock(TellerSessionMongoDataRepository.class);
  private final MongoTellerSessionRepository repo = new MongoTellerSessionRepository(data);

  @Test
  void saveCapturesSessionState() {
    TellerSessionAggregate agg = new TellerSessionAggregate("sess-1");
    agg.execute(new StartSessionCmd("teller-1", "T101"));

    repo.save(agg);
    TellerSessionDocument doc = repo.toDocument(agg);
    assertEquals("sess-1", doc.getId());
    assertEquals("ACTIVE", doc.getStatus());
    assertTrue(doc.isAuthenticated());
    assertFalse(doc.isTimedOut());
    assertTrue(doc.isNavigationStateValid());
    assertEquals(1, doc.getVersion());
    verify(data).save(any(TellerSessionDocument.class));
  }

  @Test
  void findByIdRestoresState() {
    TellerSessionDocument doc = new TellerSessionDocument();
    doc.setId("sess-2");
    doc.setStatus("ENDED");
    doc.setAuthenticated(false);
    doc.setTimedOut(true);
    doc.setInactivityTimeoutRuleViolated(true);
    doc.setNavigationStateValid(false);
    doc.setVersion(4);
    when(data.findById("sess-2")).thenReturn(Optional.of(doc));

    TellerSessionAggregate restored = repo.findById("sess-2").orElseThrow();
    assertEquals(TellerSessionAggregate.Status.ENDED, restored.getStatus());
    assertFalse(restored.isAuthenticated());
    assertTrue(restored.isTimedOut());
    assertTrue(restored.isInactivityTimeoutRuleViolated());
    assertFalse(restored.isNavigationStateValid());
    assertEquals(4, restored.getVersion());
  }

  @Test
  void findByIdHandlesNullStatus() {
    TellerSessionDocument doc = new TellerSessionDocument();
    doc.setId("sess-3");
    doc.setStatus(null);
    when(data.findById("sess-3")).thenReturn(Optional.of(doc));

    TellerSessionAggregate restored = repo.findById("sess-3").orElseThrow();
    assertEquals(TellerSessionAggregate.Status.NONE, restored.getStatus());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("nope")).thenReturn(Optional.empty());
    assertTrue(repo.findById("nope").isEmpty());
  }
}
