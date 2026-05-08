package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class S3Steps {

    private CustomerAggregate customer;
    private Exception capturedException;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        customer.enrollDirectly("cust-123", "John Doe", "john.doe@example.com", "GOV123");
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateWithInvalidEmail() {
        // Simulate a customer where the validation check for uniqueness or format would fail.
        // In this aggregate scope, we test format validation primarily.
        customer = new CustomerAggregate("cust-invalid");
        customer.enrollDirectly("cust-invalid", "Jane Doe", "jane.doe@example.com", "GOV456");
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateWithEmptyDetails() {
        customer = new CustomerAggregate("cust-empty");
        customer.enrollDirectly("cust-empty", "Existing Name", "existing@example.com", "GOV789");
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateWithActiveAccounts() {
        customer = new CustomerAggregate("cust-active");
        customer.enrollDirectly("cust-active", "Active User", "active@example.com", "GOV000");
        customer.setHasActiveAccounts(true);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Context handled in 'When' step construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Context handled in 'When' step construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Context handled in 'When' step construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // Default valid values for the positive path, overridden in specific scenarios if needed
            // However, to match the Givens, we assume the command payload varies.
            // For simplicity in this BDD, we construct a command that would pass or fail based on the aggregate state.
            
            String email = "updated@example.com";
            String name = "Updated Name";
            String dob = "1990-01-01";
            String sortCode = "123456";

            // Scenario 2: Invalid email
            if (customer.id().equals("cust-invalid")) {
                email = "invalid-email"; // Triggers format validation
            }

            // Scenario 3: Empty name/dob
            if (customer.id().equals("cust-empty")) {
                name = ""; // Triggers empty validation
                dob = "";
            }

            customer.execute(new UpdateCustomerDetailsCmd(customer.id(), name, email, dob, sortCode));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertFalse(customer.uncommittedEvents().isEmpty(), "Expected events to be emitted");
        Assertions.assertEquals("customer.details.updated", customer.uncommittedEvents().get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected domain error (IllegalArgumentException or IllegalStateException), but got: " + capturedException.getClass().getSimpleName());
    }
}
