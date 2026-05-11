package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

  private ScreenMapAggregate aggregate;
  private Command command;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_screen_map_aggregate() {
    aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @Given("a valid screenId is provided")
  public void a_valid_screen_id_is_provided() {
    // Handled in When step construction
  }

  @Given("a valid deviceType is provided")
  public void a_valid_device_type_is_provided() {
    // Handled in When step construction
  }

  @When("the RenderScreenCmd command is executed")
  public void the_render_screen_cmd_command_is_executed() {
    // Default valid command construction
    if (command == null) {
      command = new RenderScreenCmd("LOGIN_SCRN_01", "3270");
    }
    try {
      resultEvents = aggregate.execute(command);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_screen_map_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMapAggregate("screen-map-1");
    // Null or blank screenId
    command = new RenderScreenCmd(null, "3270");
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_screen_map_aggregate_that_violates_bms_constraints() {
    aggregate = new ScreenMapAggregate("screen-map-1");
    // Screen ID exceeds 8 characters (typical BMS constraint)
    command = new RenderScreenCmd("VERY_LONG_SCREEN_NAME", "3270");
  }

  @Then("a screen.rendered event is emitted")
  public void a_screen_rendered_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

    ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
    assertEquals("screen.rendered", event.type());
    assertEquals(aggregate.id(), event.aggregateId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
  }
}
