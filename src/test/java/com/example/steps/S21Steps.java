package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class S21Steps {

  private ScreenMapAggregate aggregate;
  private RenderScreenCmd cmd;
  private List<DomainEvent> resultEvents;
  private Exception capturedException;

  @Given("a valid ScreenMap aggregate")
  public void a_valid_screen_map_aggregate() {
    aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @Given("a valid screenId is provided")
  public void a_valid_screen_id_is_provided() {
    // Command construction happens in the When step for flexibility, or stored here if context requires.
    // We will construct the full command in the 'When' step based on this state.
  }

  @Given("a valid deviceType is provided")
  public void a_valid_device_type_is_provided() {
    // Same as above.
  }

  @When("the RenderScreenCmd command is executed")
  public void the_render_screen_cmd_command_is_executed() {
    // Defaults for happy path
    if (cmd == null) {
      cmd = new RenderScreenCmd("LOGIN_SCR", "3270");
    }
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  @Then("a screen.rendered event is emitted")
  public void a_screen_rendered_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
    assertEquals("screen.rendered", event.type());
    assertEquals("LOGIN_SCR", event.screenId());
    assertEquals("3270", event.deviceType());
  }

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_screen_map_aggregate_that_violates_mandatory_fields() {
    aggregate = new ScreenMapAggregate("screen-map-1");
    cmd = new RenderScreenCmd("", "3270"); // Blank screenId
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalArgumentException);
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_screen_map_aggregate_that_violates_field_lengths() {
    aggregate = new ScreenMapAggregate("screen-map-1");
    // Create a screen ID longer than 32 characters
    String longScreenId = "THIS_IS_A_VERY_LONG_SCREEN_ID_THAT_EXCEEDS_LEGACY_BMS_LIMITS";
    cmd = new RenderScreenCmd(longScreenId, "3270");
  }
}
