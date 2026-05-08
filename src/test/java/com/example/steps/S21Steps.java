package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.uimodel.model.RenderScreenCmd;
import com.example.domain.uimodel.model.ScreenMapAggregate;
import com.example.domain.uimodel.model.ScreenRenderedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private DomainEvent resultEvent;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // State stored in the command execution step
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // State stored in the command execution step
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Using valid defaults for the successful scenario
            RenderScreenCmd cmd = new RenderScreenCmd("screen-123", "3270", 80, 24);
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                this.resultEvent = events.get(0);
            }
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @When("the RenderScreenCmd command is executed with invalid inputs")
    public void theRenderScreenCmdCommandIsExecutedWithInvalidInputs() {
        // Specific setup for failure scenarios handled in Given steps would go here
        // But for simplicity, we assume the Given sets the context to throw.
        // We will drive specific failure conditions via specific Givens if needed.
        // Since the Gherkin is generic, we'll check context here.
        if (caughtException == null) {
             // Default try-catch for the generic failure hook
             try {
                // This branch is hit if the Given didn't prepare a specific failure context
                // and we are in a failure scenario. We need to trigger a failure.
                // Let's assume the context was set by the previous violation Given.
                // Actually, let's look at the next methods.
             } catch (Exception e) {
                 this.caughtException = e;
             }
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNotNull(resultEvent);
        Assertions.assertEquals("screen.rendered", resultEvent.type());
        Assertions.assertTrue(resultEvent instanceof ScreenRenderedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException
        );
    }

    // Specific violation handlers

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("map-bad-fields");
        try {
            RenderScreenCmd cmd = new RenderScreenCmd(null, "3270", 80, 24); // null screenId
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        this.aggregate = new ScreenMapAggregate("map-bad-bms");
        try {
            // Legacy constraint: max 80 chars width
            RenderScreenCmd cmd = new RenderScreenCmd("screen-999", "3270", 85, 24); 
            aggregate.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    // Bind the generic When to the specific violation logic where the exception is caught in Given
    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedFailure() {
        // Logic handled in the specific @Given violation methods to keep state clean
        // The 'caughtException' is already populated by the Given steps for violation scenarios
    }

}