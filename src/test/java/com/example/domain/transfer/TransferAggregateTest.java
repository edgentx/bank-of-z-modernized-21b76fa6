package com.example.domain.transfer;
import com.example.domain.transfer.model.*;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TransferAggregateTest {
  @Test void initiateHappyPath() {
    var t = new TransferAggregate("xfer-1");
    List<DomainEvent> events = t.execute(new InitiateTransferCmd("xfer-1", "acct-A", "acct-B", new BigDecimal("250.00"), "USD"));
    assertEquals(1, events.size());
    assertInstanceOf(TransferInitiatedEvent.class, events.get(0));
    assertEquals(TransferAggregate.Status.INITIATED, t.getStatus());
  }
  @Test void initiateRejectsSameAccount() {
    var t = new TransferAggregate("xfer-2");
    assertThrows(IllegalArgumentException.class, () -> t.execute(new InitiateTransferCmd("xfer-2", "acct-A", "acct-A", new BigDecimal("10"), "USD")));
  }
  @Test void initiateRejectsZeroAmount() {
    var t = new TransferAggregate("xfer-3");
    assertThrows(IllegalArgumentException.class, () -> t.execute(new InitiateTransferCmd("xfer-3", "acct-A", "acct-B", BigDecimal.ZERO, "USD")));
  }
  @Test void completeAfterInitiateEmitsEvent() {
    var t = new TransferAggregate("xfer-4");
    t.execute(new InitiateTransferCmd("xfer-4", "acct-A", "acct-B", new BigDecimal("100"), "USD"));
    var events = t.execute(new CompleteTransferCmd("xfer-4"));
    assertInstanceOf(TransferCompletedEvent.class, events.get(0));
    assertEquals(TransferAggregate.Status.COMPLETED, t.getStatus());
  }
  @Test void completeBeforeInitiateRejected() {
    var t = new TransferAggregate("xfer-5");
    assertThrows(IllegalStateException.class, () -> t.execute(new CompleteTransferCmd("xfer-5")));
  }
  @Test void failAfterInitiateEmitsEvent() {
    var t = new TransferAggregate("xfer-6");
    t.execute(new InitiateTransferCmd("xfer-6", "acct-A", "acct-B", new BigDecimal("100"), "USD"));
    var events = t.execute(new FailTransferCmd("xfer-6", "insufficient funds"));
    assertInstanceOf(TransferFailedEvent.class, events.get(0));
    assertEquals(TransferAggregate.Status.FAILED, t.getStatus());
  }
  @Test void completeAfterFailRejected() {
    var t = new TransferAggregate("xfer-7");
    t.execute(new InitiateTransferCmd("xfer-7", "acct-A", "acct-B", new BigDecimal("100"), "USD"));
    t.execute(new FailTransferCmd("xfer-7", "fraud"));
    assertThrows(IllegalStateException.class, () -> t.execute(new CompleteTransferCmd("xfer-7")));
  }
  @Test void initiateTwiceRejected() {
    var t = new TransferAggregate("xfer-8");
    t.execute(new InitiateTransferCmd("xfer-8", "acct-A", "acct-B", new BigDecimal("100"), "USD"));
    assertThrows(IllegalStateException.class, () -> t.execute(new InitiateTransferCmd("xfer-8", "acct-A", "acct-B", new BigDecimal("100"), "USD")));
  }
}
