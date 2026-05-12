package com.example.infrastructure.mongo.transfer;

import com.example.domain.transfer.model.CompleteTransferCmd;
import com.example.domain.transfer.model.InitiateTransferCmd;
import com.example.domain.transfer.model.TransferAggregate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoTransferRepositoryTest {

  private final TransferMongoDataRepository data = mock(TransferMongoDataRepository.class);
  private final MongoTransferRepository repo = new MongoTransferRepository(data);

  @Test
  void saveCapturesStatusEnumAndCurrency() {
    TransferAggregate agg = new TransferAggregate("xfer-1");
    agg.execute(new InitiateTransferCmd("xfer-1", "from-1", "to-1", new BigDecimal("75.00"), "GBP"));
    agg.execute(new CompleteTransferCmd("xfer-1"));

    repo.save(agg);
    TransferDocument doc = repo.toDocument(agg);
    assertEquals("xfer-1", doc.getId());
    assertEquals("from-1", doc.getFromAccountId());
    assertEquals("to-1", doc.getToAccountId());
    assertEquals(new BigDecimal("75.00"), doc.getAmount());
    assertEquals("GBP", doc.getCurrency());
    assertEquals("COMPLETED", doc.getStatus());
    assertEquals(2, doc.getVersion());
    verify(data).save(any(TransferDocument.class));
  }

  @Test
  void findByIdRestoresState() {
    TransferDocument doc = new TransferDocument();
    doc.setId("xfer-2");
    doc.setFromAccountId("from-2");
    doc.setToAccountId("to-2");
    doc.setAmount(new BigDecimal("500.00"));
    doc.setCurrency("USD");
    doc.setStatus("INITIATED");
    doc.setVersion(1);
    when(data.findById("xfer-2")).thenReturn(Optional.of(doc));

    TransferAggregate restored = repo.findById("xfer-2").orElseThrow();
    assertEquals(TransferAggregate.Status.INITIATED, restored.getStatus());
    assertEquals(new BigDecimal("500.00"), restored.getAmount());
    assertEquals("from-2", restored.getFromAccountId());
    assertEquals("to-2", restored.getToAccountId());
    assertEquals(1, restored.getVersion());
  }

  @Test
  void findByIdHandlesNullStatusGracefully() {
    TransferDocument doc = new TransferDocument();
    doc.setId("xfer-3");
    doc.setStatus(null);
    when(data.findById("xfer-3")).thenReturn(Optional.of(doc));

    TransferAggregate restored = repo.findById("xfer-3").orElseThrow();
    assertEquals(TransferAggregate.Status.NONE, restored.getStatus());
  }

  @Test
  void findByIdReturnsEmptyWhenMissing() {
    when(data.findById("missing")).thenReturn(Optional.empty());
    assertTrue(repo.findById("missing").isEmpty());
  }
}
