package com.example.steps;

import com.example.domain.screenmap.model.InputValidatedEvent;
import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    // In-memory state for the scenario
    private ScreenMapAggregate aggregate;
    private String currentScreenId = "SCREEN_001";
    private Map<String, String> currentInput = new HashMap<>();
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        // Default constructor initializes with default fields (Name, Deposit)
        this.aggregate = new ScreenMapAggregate("S-22-AGG-01");
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingMandatoryFields() {
        this.aggregate = new ScreenMapAggregate("S-22-AGG-02");
        // Clear defaults to ensure strict control over mandatory fields for the negative test
        // Or define a specific mandatory field we intend to violate
        aggregate.clearFields();
        aggregate.defineField("SSN", 9, true); // Mandatory
        aggregate.defineField("NOTE", 100, false); // Optional
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithBMSConstraints() {
        this.aggregate = new ScreenMapAggregate("S-22-AGG-03");
        aggregate.clearFields();
        // Define a field with a legacy 3270/BMS size constraint
        aggregate.defineField("ACCOUNT_NUM", 10, false);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        this.currentScreenId = "MAIN_MENU";
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Matching the default aggregate definition in S-22 (F_NAME mandatory, F_DEPOSIT optional)
        currentInput.put("F_NAME", "John Doe");
        currentInput.put("F_DEPOSIT", "1000.00");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(currentScreenId, currentInput);
        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultingEvents, "Events list should not be null");
        assertFalse(resultingEvents.isEmpty(), "At least one event should be emitted");
        
        DomainEvent event = resultingEvents.get(0);
        assertTrue(event instanceof InputValidatedEvent, "Event should be InputValidatedEvent");
        
        InputValidatedEvent validated = (InputValidatedEvent) event;
        assertEquals("input.validated", validated.type());
        assertEquals("MAIN_MENU", validated.screenId());
        assertEquals("John Doe", validated.inputFields().get("F_NAME"));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException, "Expected domain error (IllegalArgumentException)");
    }
}
