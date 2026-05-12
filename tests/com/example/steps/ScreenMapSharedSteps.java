package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

/**
 * Step definitions shared by every ScreenMap-aggregate story
 * (S-21 RenderScreen, S-22 ValidateScreenInput).
 * Each Given seeds {@link ScreenMapSharedContext#aggregate}; the
 * story-specific @When step classes read the aggregate from the same
 * context. Consolidating these here eliminates the duplicate-step-text
 * conflict that previously prevented the Cucumber suite from loading.
 */
public class ScreenMapSharedSteps {

    private final ScreenMapSharedContext ctx;

    public ScreenMapSharedSteps(ScreenMapSharedContext ctx) {
        this.ctx = ctx;
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        ctx.aggregate = new ScreenMapAggregate("screen-map-123");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // screenId is supplied when the command is constructed in @When.
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Prepared in @When.
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Prepared in @When.
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        ctx.aggregate = new ScreenMapAggregate("screen-map-mandatory-fail");
        ctx.aggregate.setMandatoryFieldsValidated(false);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsFieldLengths() {
        ctx.aggregate = new ScreenMapAggregate("screen-map-bms-fail");
        ctx.aggregate.setBmsFieldLengthCompliant(false);
    }
}
