package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.uinavigation.model.ScreenMapAggregate;
import com.example.domain.uinavigation.model.RenderScreenCmd;
import com.example.domain.uinavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Scenario 1 & 2 Setup: Valid Aggregate
    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-1");
    }

    // Scenario 3 Setup: Invalid Aggregate (BMS Constraint Violation)
    // Note: In BDD, "Given an aggregate that violates..." usually implies we are constructing
    // a command or state that will trigger the violation when executed.
    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_BMS_constraints() {
        this.aggregate = new ScreenMapAggregate("screen-2");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        // We use a specific setup command to trigger this error (nulls)
        this.aggregate = new ScreenMapAggregate("screen-3");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled implicitly by the command creation in When step for success case
    }

    @Given("a valid deviceType is provided")
    public void a valid_deviceType_is_provided() {
        // Handled implicitly by the command creation in When step for success case
    }

    // Success Case Execution
    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed_for_success() {
        // Construct valid command based on aggregate ID
        this.command = new RenderScreenCmd("screen-1", "3270", List.of());
        executeCommand();
    }

    // Failure Case Execution (Missing Fields)
    @When("the RenderScreenCmd command is executed with missing fields")
    public void the_RenderScreenCmd_command_is_executed_with_missing_fields() {
        // Construct invalid command (null deviceType)
        this.command = new RenderScreenCmd("screen-3", null, List.of());
        executeCommand();
    }

    // Failure Case Execution (BMS Constraints)
    @When("the RenderScreenCmd command is executed with long fields")
    public void the_RenderScreenCmd_command_is_executed_with_long_fields() {
        // Construct invalid command (deviceType > BMS limit)
        this.command = new RenderScreenCmd("screen-2", "DEVICE_TYPE_TOO_LONG_FOR_BMS", List.of());
        executeCommand();
    }

    private void executeCommand() {
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        // In this domain model, validation errors are IllegalArgumentExceptions
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
    }
}