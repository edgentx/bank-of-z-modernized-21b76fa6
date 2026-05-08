package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.repository.InMemoryScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class S22Steps {

    private final InMemoryScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("TEST_SCREEN_01");
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Implicitly handled by the aggregate creation or command creation
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Implicitly handled by command creation in the When step
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Create a valid command
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(
                "TEST_SCREEN_01",
                Map.of("ACCOUNT_NUM", "123456789", "TRANS_CODE", "INQ")
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("ScreenInputValidatedEvent", resultEvents.get(0).type());
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        aggregate = new ScreenMapAggregate("TEST_SCREEN_02");
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengths() {
        aggregate = new ScreenMapAggregate("TEST_SCREEN_03");
        repository.save(aggregate);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecutedWithInvalidData() {
        try {
            // This context covers both invalid scenarios based on the 'Given' context
            // Simulate missing mandatory field
            if (aggregate.id().equals("TEST_SCREEN_02")) {
                 ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(
                    "TEST_SCREEN_02",
                    Map.of("ACCOUNT_NUM", "") // Empty mandatory field
                );
                resultEvents = aggregate.execute(cmd);
            }
            // Simulate length violation
            else if (aggregate.id().equals("TEST_SCREEN_03")) {
                ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(
                    "TEST_SCREEN_03",
                    Map.of("ACCOUNT_NUM", "12345678901234567890") // Too long
                );
                resultEvents = aggregate.execute(cmd);
            }
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
    }
}
