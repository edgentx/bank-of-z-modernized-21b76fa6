package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // Setup logic handled in 'When' or combined state
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        // Setup logic handled in 'When'
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        // Create valid command defaults
        String screenId = "LOGIN_SCR"; // Valid length
        String deviceType = "MOBILE";
        String accountId = "acct-1";
        Map<String, Object> context = Map.of("lang", "en");

        this.command = new RenderScreenCmd(screenId, deviceType, accountId, context);
        
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        Assertions.assertEquals("screen.rendered", event.type());
        Assertions.assertEquals("screen-map-123", event.aggregateId());
        Assertions.assertNotNull(event.occurredAt());
        Assertions.assertEquals("MOBILE_LAYOUT_V1", event.layout());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        this.aggregate = new ScreenMapAggregate("screen-map-bad");
    }

    // We need a specific When trigger for this context, or we overload the existing one
    // Cucumber matches regex, so we can reuse or create specific steps.
    // For clarity, creating specific step bodies for the negative flow.

    @When("the RenderScreenCmd command is executed with null context")
    public void the_RenderScreenCmd_command_is_executed_with_null_context() {
        this.command = new RenderScreenCmd("LOGIN", "MOBILE", "acct1", null);
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_BMS_constraints() {
        this.aggregate = new ScreenMapAggregate("screen-map-long");
    }

    @When("the RenderScreenCmd command is executed with long screenId")
    public void the_RenderScreenCmd_command_is_executed_with_long_screenId() {
        // 9 chars > 8 limit
        this.command = new RenderScreenCmd("VERY_LONG_ID", "MOBILE", "acct1", Map.of());
        try {
            this.resultEvents = aggregate.execute(command);
        } catch (IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertTrue(thrownException.getMessage().contains("exceeds legacy BMS constraint") || 
                              thrownException.getMessage().contains("cannot be null"));
    }
}