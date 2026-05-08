package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.ui.model.ScreenMapAggregate;
import com.example.domain.ui.model.RenderScreenCmd;
import com.example.domain.ui.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private String aggregateId = "screen-123";
    private String screenId;
    private String deviceType;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        aggregate = new ScreenMapAggregate(aggregateId);
        screenId = "LOGIN_SCREEN";
        deviceType = "3270_TERMINAL";
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Handled in setup
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Handled in setup
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        try {
            Command cmd = new RenderScreenCmd(aggregateId, screenId, deviceType);
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        var events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty(), "Should have uncommitted events");
        assertTrue(events.get(0) instanceof ScreenRenderedEvent, "Event should be ScreenRenderedEvent");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        aggregate = new ScreenMapAggregate(aggregateId);
        screenId = null; // Violation
        deviceType = "3270";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Should have thrown an exception");
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_BMS_constraints() {
        aggregate = new ScreenMapAggregate(aggregateId);
        screenId = "TOO_LONG_SCREEN_ID_FOR_LEGACY_BMS"; // Violation
        deviceType = "3270";
    }
}
