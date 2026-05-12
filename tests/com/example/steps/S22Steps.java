package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Story-specific step definitions for S-22 (ValidateScreenInputCmd).
 * Shared ScreenMap Givens + the rejection @Then live in
 * {@link ScreenMapSharedSteps} / {@link CommonSteps};
 * scenario state is shared via {@link ScreenMapSharedContext}.
 */
public class S22Steps {

  private final ScreenMapSharedContext ctx;
  private final ScenarioContext sc;

  public S22Steps(ScreenMapSharedContext ctx, ScenarioContext sc) {
    this.ctx = ctx;
    this.sc = sc;
  }

  @When("the ValidateScreenInputCmd command is executed")
  public void the_validate_screen_input_cmd_command_is_executed() {
    ScreenMapAggregate aggregate = ctx.aggregate;
    if (aggregate == null) {
      aggregate = new ScreenMapAggregate("screen-map-s22");
      ctx.aggregate = aggregate;
    }
    Map<String, String> fields = new LinkedHashMap<>();
    fields.put("USERID", "TLR001");
    fields.put("AMOUNT", "100.00");
    ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(aggregate.id(), "SCR-DEPOSIT", fields);
    try {
      ctx.resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      sc.thrownException = e;
    }
  }

  @Then("a input.validated event is emitted")
  public void a_input_validated_event_is_emitted() {
    assertNull(sc.thrownException, "Should not throw exception");
    List<DomainEvent> events = ctx.resultingEvents;
    assertNotNull(events, "Events should not be null");
    assertEquals(1, events.size(), "Should emit one event");
    assertTrue(events.get(0) instanceof InputValidatedEvent, "Event type mismatch");

    InputValidatedEvent event = (InputValidatedEvent) events.get(0);
    assertEquals("input.validated", event.type());
    assertEquals(ctx.aggregate.id(), event.aggregateId());
    assertEquals("SCR-DEPOSIT", event.screenId());
    assertEquals(2, event.inputFields().size());
  }
}
