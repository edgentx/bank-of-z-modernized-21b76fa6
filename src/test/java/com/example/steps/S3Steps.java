package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.Command;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private Exception capturedException;
    private String customerId;
    private String email;
    private String sortCode;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        // Setup as enrolled with valid defaults so it passes pre-flight checks
        customer.enrollForTesting("John Doe", "john.doe@example.com", "GOV-123");
        customer.setDateOfBirth("1990-01-01"); // Ensure DOB is not empty
        customer.setActiveBankAccounts(false); // Ensure no active accounts blocking update
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        this.customerId = "cust-123";
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        this.email = "new.email@example.com";
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        this.sortCode = "10-20-30";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(customerId, "Jane Doe", email, sortCode);
            customer.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertFalse(customer.uncommittedEvents().isEmpty(), "Events list should not be empty");
        assertEquals("customer.details.updated", customer.uncommittedEvents().get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        // Setup valid base
        customer = new CustomerAggregate("cust-bad-email");
        customer.enrollForTesting("Bad Email", "invalid-email", "GOV-ID"); 
        customer.setDateOfBirth("2000-01-01");
        customer.setActiveBankAccounts(false);
        
        // Trigger: We will pass an invalid email in the command execution step context, 
        // or rely on state if the AC implies checking existing state. 
        // The AC says "violates: ... email address".
        // The Execute logic checks `c.emailAddress()`. 
        // We'll set the test input to invalid for this step.
        this.email = "invalid-new-email"; 
        this.customerId = "cust-bad-email";
        this.sortCode = "10-10-10";
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-bad-name");
        customer.enrollForTesting("Name", "name@example.com", "GOV-ID");
        customer.setActiveBankAccounts(false);
        // Violation: DOB is empty in the aggregate state
        customer.setDateOfBirth(null); 
        
        this.customerId = "cust-bad-name";
        this.email = "valid@example.com";
        this.sortCode = "10-10-10";
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-active-accounts");
        customer.enrollForTesting("Active User", "active@example.com", "GOV-ID");
        customer.setDateOfBirth("1985-05-05");
        // Violation: Active accounts exist
        customer.setActiveBankAccounts(true);

        this.customerId = "cust-active-accounts";
        this.email = "update@example.com";
        this.sortCode = "10-10-10";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // We check for domain exception types (IllegalStateException or IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
