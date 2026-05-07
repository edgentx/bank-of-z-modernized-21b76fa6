package com.example.steps;

import com.example.domain.navigation.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapAggregate screenMap;
    private String currentScreenId;
    private String currentDeviceType;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        screenMap = new ScreenMapAggregate("screen-map-1");
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        currentScreenId = "LOGIN_SCR_01";
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        currentDeviceType = "3270";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(screenMap.id(), currentScreenId, currentDeviceType);
            resultEvents = screenMap.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        screenMap = new ScreenMapAggregate("screen-map-invalid-fields");
        currentScreenId = null; // Violating mandatory field
        currentDeviceType = "3270";
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        screenMap = new ScreenMapAggregate("screen-map-invalid-bms");
        currentScreenId = "THIS_SCREEN_ID_IS_WAY_TOO_LONG_FOR_LEGACY_BMS");
        currentDeviceType = "3270";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Typically IllegalArgumentException for validation, or IllegalStateException for invariant
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
