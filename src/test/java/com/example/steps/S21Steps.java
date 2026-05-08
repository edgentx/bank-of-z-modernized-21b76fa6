package com.example.steps;

import com.example.domain.navigation.model.RenderScreenCmd;
import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenRenderedEvent;
import com.example.domain.navigation.repository.ScreenMapRepository;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S21Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private String screenId;
    private String deviceType;
    private Exception caughtException;
    private ScreenRenderedEvent lastEvent;

    // Helper to reset state per scenario
    private void resetScenario() {
        ((InMemoryScreenMapRepository) repository).clear();
        aggregate = null;
        screenId = null;
        deviceType = null;
        caughtException = null;
        lastEvent = null;
    }

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        resetScenario();
        String id = "map-001";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        resetScenario();
        String id = "map-invalid-fields";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
        // We will pass null/blank values in the 'When' step via modifying the context variables
        this.screenId = ""; // Violation: blank screenId
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBmsLengthConstraints() {
        resetScenario();
        String id = "map-invalid-length";
        aggregate = new ScreenMapAggregate(id);
        repository.save(aggregate);
        // Create a screenId that exceeds the standard legacy BMS length (e.g., > 32 chars)
        this.screenId = "THIS_IS_A_VERY_LONG_SCREEN_ID_THAT_EXCEEDS_BMS_LIMITS";
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.screenId = "LOGIN_SCREEN_01";
    }

    @And("a valid deviceType is provided")
    public void aValidDeviceTypeIsProvided() {
        this.deviceType = "3270_TERMINAL";
    }

    @When("the RenderScreenCmd command is executed")
    public void theRenderScreenCmdCommandIsExecuted() {
        try {
            // Load the aggregate fresh from the repo to ensure isolation if we were reloading,
            // but here we use the instance we created.
            // If scenario context didn't set these, defaults would be null (triggering validation errors)
            if (deviceType == null && screenId != null && !screenId.isEmpty()) {
                // This specific branch handles the specific violation scenario setup for 'valid screenId, but no device'
                // However, typically the violation scenarios override these.
                // Let's assume standard defaults if not overridden for the negative case
                if ("".equals(this.screenId) && this.deviceType == null) {
                     // specifically for the blank screenId case
                     this.deviceType = "VALID_DEVICE"; 
                }
            }
            
            // Handle specific setup for the BMS violation case to ensure device is valid
            if (this.screenId != null && this.screenId.length() > 32 && this.deviceType == null) {
                this.deviceType = "VALID_DEVICE";
            }

            RenderScreenCmd cmd = new RenderScreenCmd(aggregate.id(), this.screenId, this.deviceType);
            var events = aggregate.execute(cmd);
            if (!events.isEmpty()) {
                lastEvent = (ScreenRenderedEvent) events.get(0);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            caughtException = e;
        }
    }

    @Then("a screen.rendered event is emitted")
    public void aScreenRenderedEventIsEmitted() {
        assertNotNull(lastEvent, "Expected a ScreenRenderedEvent but none was emitted");
        assertEquals("screen.rendered", lastEvent.type());
        assertEquals(aggregate.id(), lastEvent.aggregateId());
        assertEquals(screenId, lastEvent.screenId());
        assertEquals(deviceType, lastEvent.deviceType());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown, but command succeeded");
        assertTrue(caughtException instanceof IllegalArgumentException, "Expected IllegalArgumentException for domain violation");
        // Verify message content for clarity
        String message = caughtException.getMessage().toLowerCase();
        boolean matchesMandatory = message.contains("mandatory") || message.contains("required");
        boolean matchesBMS = message.contains("bms") || message.contains("length");
        
        assertTrue(matchesMandatory || matchesBMS, "Exception message did not match expected violation: " + caughtException.getMessage());
    }
}
