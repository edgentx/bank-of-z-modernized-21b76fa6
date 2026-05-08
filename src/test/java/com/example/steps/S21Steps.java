package com.example.steps;

import com.example.domain.screenmap.model.RenderScreenCmd;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ScreenRenderedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapAggregate aggregate;
    private RenderScreenCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.aggregate = new ScreenMapAggregate("LOGIN01"); // Valid length (7)
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // screenId is implicitly part of the aggregate context, but we construct the command here
        // For this scenario, we use the aggregate's ID
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        // Handled in the When clause construction for now, or we could store state
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        // Construct a valid command based on the aggregate state
        cmd = new RenderScreenCmd(aggregate.id(), "3270");
        executeCommand();
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals(ScreenRenderedEvent.class, resultEvents.get(0).getClass());
        assertEquals("screen.rendered", resultEvents.get(0).type());
    }

    // --- Scenario 2: Missing Mandatory Fields ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryInputs() {
        this.aggregate = new ScreenMapAggregate("LOGIN01");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedInvalidInput() {
        // Violation: null/blank deviceType
        cmd = new RenderScreenCmd(aggregate.id(), ""); 
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("mandatory"));
    }

    // --- Scenario 3: BMS Constraint Violation ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLength() {
        this.aggregate = new ScreenMapAggregate("LOGIN01");
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecutedLongId() {
        // Violation: screenId > 8 chars (e.g., "MAIN_MENU_DASHBOARD")
        cmd = new RenderScreenCmd("MAIN_MENU_DASH", "3270");
        executeCommand();
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainErrorBMS() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalArgumentException);
        assertTrue(thrownException.getMessage().contains("BMS constraint"));
    }

    // Helper
    private void executeCommand() {
        try {
            this.resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }
}
