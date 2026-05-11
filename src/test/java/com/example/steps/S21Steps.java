package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
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

  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void a_screen_map_aggregate_with_mandatory_violations() {
    aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void a_screen_map_aggregate_with_length_violations() {
    aggregate = new ScreenMapAggregate("screen-map-1");
  }

  @And("a valid screenId is provided")
  public void a_valid_screen_id_is_provided() {
    // Screen ID will be set in the When clause or stored here
    // For the sake of step isolation, we define valid data here
    // but construct the command in the 'When' step based on the scenario context.
  }

  @And("a valid deviceType is provided")
  public void a_valid_device_type_is_provided() {
    // Device type will be set in the When clause
  }

  @When("the RenderScreenCmd command is executed")
  public void the_render_screen_cmd_command_is_executed() {
    // Default valid command construction for the success scenario
    // or override for specific scenarios if we passed parameters, 
    // but Gherkin scenarios imply specific state setup in 'Given'.
    // We'll assume standard valid data here unless the scenario context implies otherwise
    // (which we handle via exception capturing or specific setup logic).
    // Since Gherkin doesn't pass args to When, we rely on context. 
    // To keep it simple, we construct valid command here. 
    // The validation scenarios rely on the aggregate throwing errors.
    
    // To make the negative scenarios work, we need to construct invalid commands.
    // However, standard Cucumber Java practice often sets state in Given.
    // Given the constraints, we will try a valid command first. 
    // For the negative tests, we would technically need to set the command fields to invalid values.
    // Since we can't modify the Gherkin, we assume the test context implies the command content.
    // We will check the context in a real app, but here we will cheat slightly:
    // If the aggregate was created with 'violates', we might need an invalid command.
    // Since the 'violates' clause modifies the Aggregate state description, 
    // let's assume the Command is standard but the Data is not? 
    // Actually, the constraint violation scenarios usually mean the COMMAND sent is bad.
    // But the Gherkin says "Aggregate that violates".
    // Let's assume the Command defaults to VALID for 'When'.
    // If we are in a negative test, we need a different command. 
    // Since we can't detect scenario easily without a shared state flag, 
    // we will try executing. The 'Given' for negative tests implies we should be constructing bad commands? 
    // No, "Aggregate that violates" implies Aggregate State.
    // BUT the criteria is "Validate BEFORE screen submission".
    // Let's assume the 'When' creates a VALID command for the happy path.
    // For the error paths, we will construct specific commands to trigger the errors.
    
    // Improvisation for error scenarios:
    // We will attempt to execute. If we are in a test expecting an error,
    // we rely on the Test Runner context. 
    // Since we can't switch scenarios in code easily, 
    // we will just run the Happy Path command here. 
    // *Wait*, this will break the negative tests.
    
    // Strategy: Check if the `capturedException` is null. 
    // We'll actually just define the command here as valid.
    // The negative tests will need to set `cmd` explicitly in their specific context? 
    // We can't inject context into Steps easily without extra boilerplate.
    // Alternative: The 'Given' for negative tests sets a flag or overrides the command.
    
    // Let's just define a valid command.
    if (this.cmd == null) {
       this.cmd = new RenderScreenCmd("LOGIN_SCR_01", "DESKTOP");
    }
    
    try {
      resultEvents = aggregate.execute(cmd);
    } catch (Exception e) {
      capturedException = e;
    }
  }

  // We need to hook into the 'Given' violations to actually fail.
  // Since the text says "Aggregate that violates", but the error is command validation,
  // we might need to simulate the command being bad.
  // I will add internal state management for the negative scenarios.
  
  // Scenario 2 Hook
  @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
  public void setupMandatoryViolation() {
    a_valid_screen_map_aggregate();
    this.cmd = new RenderScreenCmd("", "DESKTOP"); // Blank screen ID
  }

  // Scenario 3 Hook
  @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
  public void setupLengthViolation() {
    a_valid_screen_map_aggregate();
    // Create a screen ID longer than 80 chars
    String longId = "A".repeat(100);
    this.cmd = new RenderScreenCmd(longId, "DESKTOP");
  }

  @Then("a screen.rendered event is emitted")
  public void a_screen_rendered_event_is_emitted() {
    assertNotNull(resultEvents);
    assertEquals(1, resultEvents.size());
    assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
    assertEquals("screen.rendered", event.type());
    assertEquals("screen-map-1", event.aggregateId());
  }

  @Then("the command is rejected with a domain error")
  public void the_command_is_rejected_with_a_domain_error() {
    assertNotNull(capturedException);
    assertTrue(capturedException instanceof IllegalArgumentException);
  }

  @When("the RenderScreenCmd command is executed")
  public void the_render_screen_cmd_command_is_executed_negative() {
    // This is handled by the generic @When above, but we use the cmd setup in @Given.
    // Just ensuring the link is clear. The generic @When works because `cmd` is populated.
  }

}
