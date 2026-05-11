package com.example.steps;

import com.example.domain.routing.model.ScreenMap;
import com.example.domain.routing.model.ScreenInputValidatedEvent;
import com.example.domain.routing.model.ValidateScreenInputCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMap aggregate;
    private ValidateScreenInputCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Standard Screen Definition for BMS constraints (Legacy COBOL mapping)
    // e.g., ACCOUNT-NUMBER PIC X(10).
    private static final int BMS_MAX_LENGTH_ACCT = 10;
    private static final int BMS_MAX_LENGTH_NAME = 30;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMap("S-LOGIN-01");
        // Define standard fields for this screen
        aggregate.defineField("USER_ID", true, BMS_MAX_LENGTH_ACCT); // Mandatory, length 10
        aggregate.defineField("PASSWORD", true, 20); // Mandatory
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled in command construction
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "ALICE01"); // Length 8 (< 10)
        inputs.put("PASSWORD", "s3cr3t!");
        this.cmd = new ValidateScreenInputCmd("S-LOGIN-01", inputs);
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
        ScreenInputValidatedEvent event = (ScreenInputValidatedEvent) resultEvents.get(0);
        Assertions.assertEquals("input.validated", event.type());
        Assertions.assertEquals("S-LOGIN-01", event.aggregateId());
    }

    // --- Scenario 2: Mandatory Fields ---

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesAllMandatoryInputFieldsMustBeValidatedBeforeScreenSubmission() {
        aggregate = new ScreenMap("S-LOGIN-01");
        aggregate.defineField("USER_ID", true, 10); // Mandatory
        aggregate.defineField("PASSWORD", true, 20); // Mandatory

        // Create command missing the mandatory PASSWORD
        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "BOB01");
        // Password is missing
        this.cmd = new ValidateScreenInputCmd("S-LOGIN-01", inputs);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertTrue(thrownException.getMessage().contains("mandatory"));
    }

    // --- Scenario 3: BMS Constraints ---

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesFieldLengthsMustStrictlyAdhereToLegacyBMSConstraintsDuringTheTransitionPeriod() {
        aggregate = new ScreenMap("S-LOGIN-01");
        aggregate.defineField("USER_ID", true, 10); // Legacy BMS Max Length 10

        Map<String, String> inputs = new HashMap<>();
        inputs.put("USER_ID", "VERY_LONG_USERNAME"); // Length 17
        this.cmd = new ValidateScreenInputCmd("S-LOGIN-01", inputs);
    }
