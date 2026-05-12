package com.example.domain.screenmap;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScreenMapAggregateTest {

  @Test void renderScreenHappyPathEmitsEvent() {
    var agg = new ScreenMapAggregate("screen-map-1");
    List<DomainEvent> events = agg.execute(new RenderScreenCmd("screen-map-1", "SCR-LOGIN", "3270"));
    assertEquals(1, events.size());
    assertInstanceOf(ScreenRenderedEvent.class, events.get(0));
    assertEquals("screen.rendered", events.get(0).type());
    assertEquals("screen-map-1", events.get(0).aggregateId());
    assertEquals(1, agg.getVersion());
  }

  @Test void renderScreenRejectsWhenMandatoryFieldsUnvalidated() {
    var agg = new ScreenMapAggregate("screen-map-2");
    agg.setMandatoryFieldsValidated(false);
    assertThrows(IllegalStateException.class,
      () -> agg.execute(new RenderScreenCmd("screen-map-2", "SCR-DEPOSIT", "3270")));
  }

  @Test void renderScreenRejectsWhenBmsFieldLengthsNonCompliant() {
    var agg = new ScreenMapAggregate("screen-map-3");
    agg.setBmsFieldLengthCompliant(false);
    assertThrows(IllegalStateException.class,
      () -> agg.execute(new RenderScreenCmd("screen-map-3", "SCR-XFER", "WEB")));
  }

  @Test void renderScreenCmdRejectsBlankScreenId() {
    assertThrows(IllegalArgumentException.class,
      () -> new RenderScreenCmd("screen-map-4", "", "3270"));
  }

  @Test void renderScreenCmdRejectsBlankDeviceType() {
    assertThrows(IllegalArgumentException.class,
      () -> new RenderScreenCmd("screen-map-5", "SCR-LOGIN", ""));
  }
}
