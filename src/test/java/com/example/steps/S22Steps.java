package com.example.steps;

import com.example.domain.navigation.model.ScreenMapAggregate;
import com.example.domain.navigation.model.ScreenInputValidatedEvent;
import com.example.domain.navigation.model.ValidateScreenInputCmd;
import com.example.domain.navigation.repository.InMemoryScreenMapRepository;
import com.example.domain.navigation.repository.ScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private ScreenMapAggregate aggregate;
    private String screenId;
    private Map<String, String> inputFields = new HashMap<>();
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        this.screenId = "LOGIN_SCREEN";
        this.aggregate = new ScreenMapAggregate(screenId);
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // screenId initialized in previous step
        Assertions.assertNotNull(screenId);
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        inputFields.put("USER_ID", "ALICE");
        inputFields.put("PASSWORD", "SECRET");
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Reload aggregate to simulate persistence fetch
            var agg = repository.findById(screenId).orElseThrow();
            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd(screenId, inputFields);
            resultEvents = agg.execute(cmd);
            repository.save(agg);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNull(thrownException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof ScreenInputValidatedEvent);
    }

    // ---------- Rejection Scenarios ----------

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateThatViolatesMandatoryFields() {
        this.screenId = "TRANSFER_SCREEN";
        this.aggregate = new ScreenMapAggregate(screenId);
        repository.save(aggregate);
        // Intentionally leave inputFields empty or missing mandatory keys
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateThatViolatesBMSLength() {
        this.screenId = "LONG_INPUT_SCREEN";
        this.aggregate = new ScreenMapAggregate(screenId);
        repository.save(aggregate);
        
        // Create a massive string to violate BMS length check (max 32000 defined in aggregate)
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<33000; i++) {
            sb.append("X");
        }
        inputFields.put("LONG_FIELD", sb.toString());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected exception but none was thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
    }
}
