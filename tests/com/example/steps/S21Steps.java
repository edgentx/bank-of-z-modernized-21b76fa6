package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

  private ScreenMapAggregate aggregate;
  private RenderScreenCmd cmd;
  private Exception thrownException;
  private List<DomainEvent> resultingEvents;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_screen_map_aggregate() {
    aggregate = new ScreenMapAggregate("screen-map-123");
  }

  @And("a valid screenId is provided")
  public void a_valid_screen_id_is_provided() {
    // Prepared in 'When' step
  }

  @And("a valid deviceType is provided")
  public void a_valid_device_type_is_provided() {
    // Prepared in 'When' step
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_screen_map_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMapAggregate("screen-map-mandatory-fail");
    aggregate.setMandatoryFieldsValidated(false);
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_screen_map_aggregate_that_violates_bms_field_lengths() {
    aggregate = new ScreenMapAggregate("screen-map-bms-fail");
    aggregate.setBmsFieldLengthCompliant(false);
  }

  @When("the RenderScreenCmd command is executed")
  public void the_render_screen_cmd_command_is_executed() {
    if (aggregate == null) {
      aggregate = new ScreenMapAggregate("screen-map-123");
    }
    cmd = new RenderScreenCmd(aggregate.id(), "SCR-LOGIN", "3270");
    try {
      resultingEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("a screen.rendered event is emitted")
  public void a_screen_rendered_event_is_emitted() {
    assertNull(thrownException, "Should not throw exception");
    assertNotNull(resultingEvents, "Events should not be null");
    assertEquals(1, resultingEvents.size(), "Should emit one event");
    assertTrue(resultingEvents.get(0) instanceof ScreenRenderedEvent, "Event type mismatch");

    ScreenRenderedEvent event = (ScreenRenderedEvent) resultingEvents.get(0);
    assertEquals("screen.rendered", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
    assertEquals("SCR-LOGIN", event.screenId());
    assertEquals("3270", event.deviceType());
  }
}
