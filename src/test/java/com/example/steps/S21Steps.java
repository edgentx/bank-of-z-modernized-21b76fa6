package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.userinterface.model.RenderScreenCmd;
import com.example.domain.userinterface.model.ScreenMapAggregate;
import com.example.domain.userinterface.repository.InMemoryScreenMapRepository;
import com.example.domain.userinterface.repository.ScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception caughtException;
    private Iterable<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Context handled in 'When' clause via command construction
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Context handled in 'When' clause
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        RenderScreenCmd cmd = new RenderScreenCmd(
                "screen-map-1",
                "LOGIN_SCREEN",
                "DESKTOP",
                Map.of("username", "testuser", "password", "secret")
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertTrue(resultEvents.iterator().hasNext());
        assertEquals("screen.rendered", resultEvents.iterator().next().type());
        assertNull(caughtException);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFields() {
        aggregate = new ScreenMapAggregate("invalid-1");
    }

    @When("the RenderScreenCmd command is executed with missing fields")
    public void theCommandIsExecutedWithMissingFields() {
        // Pass null fields to simulate validation failure
        RenderScreenCmd cmd = new RenderScreenCmd(
                "invalid-1",
                "LOGIN_SCREEN",
                "DESKTOP",
                null // Violates mandatory fields check
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException);
        assertTrue(caughtException.getMessage().contains("validated") || caughtException.getMessage().contains("mandatory"));
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("bms-violator");
    }

    @When("the RenderScreenCmd command is executed with long fields")
    public void theCommandIsExecutedWithLongFields() {
        // Create a field value exceeding 80 chars
        String longValue = "a".repeat(81);
        RenderScreenCmd cmd = new RenderScreenCmd(
                "bms-violator",
                "FORM_SCREEN",
                "3270",
                Map.of("description", longValue)
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            caughtException = e;
        }
    }
}
