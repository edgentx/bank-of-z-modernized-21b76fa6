package com.example.domain.transaction;
import com.example.domain.transaction.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryTransactionRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TransactionAggregateTest {
  @Test void depositHappyPath() {
    var t = new TransactionAggregate("tx-1");
    List<DomainEvent> events = t.execute(new PostDepositCmd("tx-1", "acct-1", new BigDecimal("100.00"), "USD"));
    assertEquals(1, events.size());
    var e = (TransactionPostedEvent) events.get(0);
    assertEquals("deposit", e.kind());
    assertEquals(new BigDecimal("100.00"), e.amount());
    assertTrue(t.isPosted());
  }
  @Test void withdrawalHappyPath() {
    var t = new TransactionAggregate("tx-2");
    t.execute(new PostWithdrawalCmd("tx-2", "acct-1", new BigDecimal("50.00"), "USD"));
    assertEquals("withdrawal", t.getKind());
    assertEquals(1, t.getVersion());
  }
  @Test void postedTwiceRejected() {
    var t = new TransactionAggregate("tx-3");
    t.execute(new PostDepositCmd("tx-3", "acct-1", new BigDecimal("10"), "USD"));
    assertThrows(IllegalStateException.class, () -> t.execute(new PostDepositCmd("tx-3", "acct-1", new BigDecimal("10"), "USD")));
  }
  @Test void zeroAmountRejected() {
    var t = new TransactionAggregate("tx-4");
    assertThrows(IllegalArgumentException.class, () -> t.execute(new PostDepositCmd("tx-4", "acct-1", BigDecimal.ZERO, "USD")));
  }
  @Test void invalidCurrencyRejected() {
    var t = new TransactionAggregate("tx-5");
    assertThrows(IllegalArgumentException.class, () -> t.execute(new PostDepositCmd("tx-5", "acct-1", new BigDecimal("5"), "DOLLAR")));
  }
  @Test void reverseAfterPostEmitsEvent() {
    var t = new TransactionAggregate("tx-6");
    t.execute(new PostDepositCmd("tx-6", "acct-1", new BigDecimal("100"), "USD"));
    var events = t.execute(new ReverseTransactionCmd("tx-6", "duplicate detected"));
    assertEquals(1, events.size());
    assertInstanceOf(TransactionReversedEvent.class, events.get(0));
    assertTrue(t.isReversed());
  }
  @Test void reverseUnpostedRejected() {
    var t = new TransactionAggregate("tx-7");
    assertThrows(IllegalStateException.class, () -> t.execute(new ReverseTransactionCmd("tx-7", "no reason")));
  }
  @Test void reverseTwiceRejected() {
    var t = new TransactionAggregate("tx-8");
    t.execute(new PostDepositCmd("tx-8", "acct-1", new BigDecimal("100"), "USD"));
    t.execute(new ReverseTransactionCmd("tx-8", "first"));
    assertThrows(IllegalStateException.class, () -> t.execute(new ReverseTransactionCmd("tx-8", "second")));
  }
  @Test void mockRepositoryRoundTrip() {
    var repo = new InMemoryTransactionRepository();
    var t = new TransactionAggregate("tx-9");
    t.execute(new PostDepositCmd("tx-9", "acct-1", new BigDecimal("42.00"), "EUR"));
    repo.save(t);
    assertEquals(new BigDecimal("42.00"), repo.findById("tx-9").orElseThrow().getAmount());
  }
}
