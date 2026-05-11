package com.example.steps;

import com.example.domain.routing.model.InputValidatedEvent;
import com.example.domain.routing.model.ScreenMapAggregate;
import com.example.domain.routing.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    // Standard constraints for a 'Login' screen used in tests
    // BMS legacy constraints: USER_ID(8), PASSWORD(24)
    private static final String SCREEN_MAP_ID = "SM-LOGIN-01";
    private static final Map<String, Integer> CONSTRAINTS = Map.of(
            "USER_ID", 8,
            "PASSWORD", 24
    );
    private static final List<String> MANDATORY = List.of("USER_ID", "PASSWORD");

    private ScreenMapAggregate aggregate;
    private final InMemoryScreenMapRepository repo = new InMemoryScreenMapRepository();
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate(SCREEN_MAP_ID, CONSTRAINTS, MANDATORY);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingMandatoryFields() {
        aggregate = new ScreenMapAggregate(SCREEN_MAP_ID, CONSTRAINTS, MANDATORY);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithExcessiveFieldLengths() {
        aggregate = new ScreenMapAggregate(SCREEN_MAP_ID, CONSTRAINTS, MANDATORY);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // ScreenId is part of the command, handled in 'When'
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Handled in 'When'
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        // Default valid inputs for the success scenario
        executeCommand(Map.of("USER_ID", "validUser", "PASSWORD", "secret"), "SCR-001");
    }

    @When("the ValidateScreenInputCmd command is executed with missing mandatory field")
    public void theValidateScreenInputCmdCommandIsExecutedWithMissingField() {
        // Missing PASSWORD
        executeCommand(Map.of("USER_ID", "validUser"), "SCR-001");
    }

    @When("the ValidateScreenInputCmd command is executed with invalid field length")
    public void theValidateScreenInputCmdCommandIsExecutedWithInvalidLength() {
        // USER_ID exceeds 8 chars
        executeCommand(Map.of("USER_ID", "userTooLong", "PASSWORD", "secret"), "SCR-001");
    }

    private void executeCommand(Map<String, String> inputs, String screenId) {
        try {
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(SCREEN_MAP_ID, screenId, inputs);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof InputValidatedEvent);
        
        InputValidatedEvent event = (InputValidatedEvent) resultEvents.get(0);
        assertEquals("input.validated", event.type());
        assertEquals(SCREEN_MAP_ID, event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
