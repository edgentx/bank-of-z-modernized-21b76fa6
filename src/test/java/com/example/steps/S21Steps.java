package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-21: RenderScreenCmd.
 */
@SpringBootTest
public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd command;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("screen-map-1");
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Default valid screenId for positive flow, or prepare state for param injection
        // Note: In a real framework, these might set instance variables used to build the command
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Default valid deviceType
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // We assume a standard valid command is executed unless specific state was set (not shown in simple Given)
        // For the sake of this structure, we default to valid values.
        if (command == null) {
            command = new RenderScreenCmd("LOGIN_SCR", "WEB_TERMINAL");
        }
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof ScreenRenderedEvent);

        ScreenRenderedEvent event = (ScreenRenderedEvent) resultEvents.get(0);
        assertEquals("screen.rendered", event.type());
        assertEquals("LOGIN_SCR", event.screenId());
        assertEquals("WEB_TERMINAL", event.deviceType());
    }

    // Rejection Scenarios

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("screen-map-err-1");
        command = new RenderScreenCmd(null, "WEB_TERMINAL"); // Null screenId
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsConstraints() {
        aggregate = new ScreenMapAggregate("screen-map-err-2");
        // Screen ID > 8 chars (Constraint check in Aggregate)
        command = new RenderScreenCmd("VERY_LONG_SCREEN_NAME", "MOBILE");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
