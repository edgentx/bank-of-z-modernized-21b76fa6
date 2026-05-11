package com.example.steps;

import com.example.domain.shared.Aggregate;
import com.example.domain.userinterface.model.*;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import com.example.domain.shared.UnknownCommandException;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private Aggregate aggregate;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // We assume the ID "LOGIN_SCR_01" exists in our in-memory repo
        // for the happy path scenario.
        this.aggregate = repository.findById("LOGIN_SCR_01");
        Assertions.assertNotNull(aggregate);
    }

    @Given("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context: The aggregate is already loaded in the previous step.
        // The RenderScreenCmd will reference this aggregate's ID.
    }

    @Given("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context: Will be passed in the command.
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Assuming aggregate ID is "LOGIN_SCR_01" and device is valid
            RenderScreenCmd cmd = new RenderScreenCmd("LOGIN_SCR_01", DeviceType.TERMINAL_3270);
            List<com.example.domain.shared.DomainEvent> events = aggregate.execute(cmd);
            Assertions.assertNotNull(events);
            Assertions.assertFalse(events.isEmpty());
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        // We verify the internal state or event list via the aggregate API
        Assertions.assertTrue(((ScreenMapAggregate) aggregate).isRendered());
    }

    // --- Negative Scenarios ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        // Setup an aggregate where inputs are missing
        // For example, ID is null/blank
        this.aggregate = new ScreenMapAggregate(null); 
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        // Setup an aggregate with fields exceeding BMS limits (e.g. ID > 8 chars)
        String longScreenId = "SCREEN_ID_THAT_IS_TOO_LONG_FOR_BMS";
        this.aggregate = new ScreenMapAggregate(longScreenId);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected a domain error (exception) to be thrown");
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException ||
            caughtException instanceof UnknownCommandException
        );
    }
}
