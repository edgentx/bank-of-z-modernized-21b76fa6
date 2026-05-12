package com.example.infrastructure.mongo.support;

import com.example.domain.customer.model.CustomerAggregate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AggregateFieldAccessTest {

  @Test
  void setAndGetOnAggregateOwnField() {
    CustomerAggregate agg = new CustomerAggregate("c-1");
    AggregateFieldAccess.set(agg, "email", "a@b.com");
    assertEquals("a@b.com", AggregateFieldAccess.get(agg, "email"));
  }

  @Test
  void setReachesSuperclassField() {
    CustomerAggregate agg = new CustomerAggregate("c-2");
    AggregateFieldAccess.set(agg, "version", 7);
    assertEquals(7, agg.getVersion());
    assertEquals(7, AggregateFieldAccess.get(agg, "version"));
  }

  @Test
  void unknownFieldRaisesIllegalState() {
    CustomerAggregate agg = new CustomerAggregate("c-3");
    assertThrows(IllegalStateException.class, () -> AggregateFieldAccess.set(agg, "doesNotExist", "x"));
    assertThrows(IllegalStateException.class, () -> AggregateFieldAccess.get(agg, "doesNotExist"));
  }
}
