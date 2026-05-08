package com.example.steps;

import com.example.domain.screenmap.model.ScreenMapAggregate;
import com.example.domain.screenmap.model.ValidateScreenInputCmd;
import com.example.domain.screenmap.repository.ScreenMapRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryScreenMapRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S22Steps {

    private ScreenMapAggregate aggregate;
    private final ScreenMapRepository repository = new InMemoryScreenMapRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid ScreenMap aggregate")
    public void aValidScreenMapAggregate() {
        aggregate = new ScreenMapAggregate("S-22");
        aggregate.defineField("ACCOUNT", 10, true);
        aggregate.defineField("NAME", 30, true);
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: All mandatory input fields must be validated before screen submission.")
    public void aScreenMapAggregateWithMissingMandatory() {
        aggregate = new ScreenMapAggregate("S-22");
        aggregate.defineField("ACCOUNT", 10, true); // Mandatory
        aggregate.defineField("NAME", 30, true);    // Mandatory
        repository.save(aggregate);
    }

    @Given("a ScreenMap aggregate that violates: Field lengths must strictly adhere to legacy BMS constraints during the transition period.")
    public void aScreenMapAggregateWithLengthViolation() {
        aggregate = new ScreenMapAggregate("S-22");
        aggregate.defineField("ACCOUNT", 5, true);
        aggregate.defineField("NAME", 10, true);
        repository.save(aggregate);
    }

    @And("a valid screenId is provided")
    public void aValidScreenIdIsProvided() {
        // Handled implicitly by the aggregate initialization
    }

    @And("a valid inputFields is provided")
    public void aValidInputFieldsIsProvided() {
        // Inputs provided in the When block
    }

    @When("the ValidateScreenInputCmd command is executed")
    public void theValidateScreenInputCmdCommandIsExecuted() {
        try {
            // Default valid inputs for success case
            Map<String, String> inputs = new HashMap<>();
            inputs.put("ACCOUNT", "1234567890");
            inputs.put("NAME", "John Doe");

            // Override specific inputs for violation scenarios based on context
            // (Ideally, Cucumber tables would parameterize this, but simplified here)
            if (aggregate.getClass().getDeclaredFields().length > 0) { 
                // Check specific scenario context hints via aggregate state if needed, 
                // but here we rely on the specific Given methods to set up the aggregate constraints.
                // We assume valid inputs for the 'Success' scenario.
            }

            // To differentiate the failure scenarios in this simplified step, we check the context
            // Scenario 2: Missing mandatory
            if (aggregate.toString().contains("MissingMandatory")) {
                inputs.put("NAME", ""); // Clear mandatory field
            }

            // Scenario 3: Length violation
            if (aggregate.toString().contains("LengthViolation")) {
                inputs.put("ACCOUNT", "123456789012345"); // Exceeds length 5/10
            }

            ValidateScreenInputCmd cmd = new ValidateScreenInputCmd("S-22", inputs);
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a input.validated event is emitted")
    public void aInputValidatedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("input.validated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException);
    }

    // Helper methods to simulate state/context tracking if strictly needed without tables
    private boolean isMandatoryViolationContext = false;
    private boolean isLengthViolationContext = false;

    // We can refine the When logic by setting flags in the Given methods
    public void setMandatoryViolationContext() { isMandatoryViolationContext = true; }
    public void setLengthViolationContext() { isLengthViolationContext = true; }
}
