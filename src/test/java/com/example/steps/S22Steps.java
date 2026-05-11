package com.example.steps;

import com.example.domain.userinterfacenavigation.model.ScreenMapAggregate;
import com.example.domain.userinterfacenavigation.model.ValidateScreenInputCmd;
import com.example.domain.userinterfacenavigation.model.ScreenInputValidatedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.userinterfacenavigation.repository.ScreenMapRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private String screenId;
    private Map<String, String> inputFields;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    // In-memory repository simulation for context if needed, though we use direct instantiation here for simplicity
    // This mimics the structure found in S10Steps.java

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.screenId = "LOGIN_SCREEN";
        this.aggregate = new ScreenMapAggregate(this.screenId);
        this.capturedException = null;
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // screenId is already set in the aggregate constructor
        assertNotNull(this.screenId);
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        this.inputFields = new HashMap<>();
        this.inputFields.put("ACCOUNT_NO", "123456789"); // Valid length < 10
        this.inputFields.put("TRANS_AMT", "100.00");      // Valid length < 12
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingMandatoryField() {
        this.screenId = "LOGIN_SCREEN";
        this.aggregate = new ScreenMapAggregate(this.screenId);
        this.inputFields = new HashMap<>();
        // ACCOUNT_NO is mandatory, but we leave it out
        this.inputFields.put("REF_NO", "OptionalRef");
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithInvalidLengthField() {
        this.screenId = "LOGIN_SCREEN";
        this.aggregate = new ScreenMapAggregate(this.screenId);
        this.inputFields = new HashMap<>();
        // ACCOUNT_NO max length is 10
        this.inputFields.put("ACCOUNT_NO", "1234567890123"); // Length 13
        this.inputFields.put("TRANS_AMT", "50.00");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(this.screenId, this.inputFields);
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(this.capturedException, "Should not have thrown an exception");
        assertNotNull(this.resultingEvents);
        assertEquals(1, this.resultingEvents.size());
        assertTrue(this.resultingEvents.get(0) instanceof ScreenInputValidatedEvent);
        
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) this.resultingEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(this.screenId, event.aggregateId());
        assertEquals(this.inputFields, event.inputFields());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(this.capturedException);
        assertTrue(this.capturedException instanceof IllegalStateException);
        // Verify error message content based on the scenario (though broadly checking for StateException is sufficient for BDD step)
        assertTrue(this.capturedException.getMessage().contains("mandatory") || 
                   this.capturedException.getMessage().contains("BMS"));
    }
}
