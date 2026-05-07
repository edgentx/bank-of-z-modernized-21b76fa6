package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.model.RenderScreenCmd;
import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private String deviceType;
    private Map<String, String> mandatoryFields;
    private Map<String, String> fieldContent;
    private List<DomainEvent> resultEvents;
    private Exception domainException;

    @Given("a valid ScreenMap aggregate")
    public void a_valid_ScreenMap_aggregate() {
        screenId = "LOGIN_SCREEN";
        aggregate = new ScreenMapAggregate(screenId);
        mandatoryFields = new HashMap<>();
        fieldContent = new HashMap<>();
    }

    @Given("a valid screenId is provided")
    public void a_valid_screenId_is_provided() {
        // screenId already initialized
    }

    @Given("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided() {
        deviceType = "DESKTOP";
    }

    @And("a valid screenId is provided")
    public void a_valid_screenId_is_provided_and() {
        // Alias for above
    }

    @And("a valid deviceType is provided")
    public void a_valid_deviceType_is_provided_and() {
        // Alias for above
    }

    @When("the RenderScreenCmd command is executed")
    public void the_RenderScreenCmd_command_is_executed() {
        RenderScreenCmd cmd = new RenderScreenCmd(screenId, deviceType, mandatoryFields, fieldContent);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            domainException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void a_screen_rendered_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);
        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals(screenId, event.aggregateId());
        assertNotNull(event.layout());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void a_ScreenMap_aggregate_that_violates_mandatory_fields() {
        screenId = "FORM_SCREEN";
        aggregate = new ScreenMapAggregate(screenId);
        deviceType = "MOBILE";
        mandatoryFields = new HashMap<>();
        mandatoryFields.put("username", ""); // Violation: blank
        mandatoryFields.put("password", "secret");
        fieldContent = new HashMap<>();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(domainException);
        assertTrue(domainException instanceof IllegalArgumentException || domainException instanceof IllegalStateException);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void a_ScreenMap_aggregate_that_violates_field_lengths() {
        screenId = "LONG_INPUT_SCREEN";
        aggregate = new ScreenMapAggregate(screenId);
        deviceType = "TERMINAL";
        mandatoryFields = new HashMap<>();
        mandatoryFields.put("notes", "valid");
        fieldContent = new HashMap<>();
        // Create a string > 80 chars
        fieldContent.put("notes", "A".repeat(81)); // Violation: length > 80
    }
}