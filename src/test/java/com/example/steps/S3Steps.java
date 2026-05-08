package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerEnrolledEvent;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private String customerId;
    private String emailAddress;
    private String sortCode;
    private String fullName;
    private String dateOfBirth;
    private String governmentId;
    private boolean hasActiveBankAccounts = false;

    private Throwable thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        // Enroll the customer first to make it valid for updates
        customer.execute(new EnrollCustomerCmd(customerId, "John Doe", "john.doe@example.com", "GOV-ID-123"));
        // Clear events from enrollment so we only check update events
        customer.clearEvents();
        
        // Set defaults for command fields
        fullName = "John Doe";
        dateOfBirth = "1990-01-01";
        governmentId = "GOV-ID-123";
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        customerId = "cust-123";
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        emailAddress = "new.email@example.com";
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        sortCode = "12-34-56";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            var cmd = new UpdateCustomerDetailsCmd(
                customerId, 
                emailAddress, 
                sortCode, 
                fullName, 
                dateOfBirth, 
                governmentId,
                hasActiveBankAccounts
            );
            resultingEvents = customer.execute(cmd);
        } catch (Throwable t) {
            thrownException = t;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultingEvents.get(0);
        assertEquals(emailAddress, event.emailAddress());
        assertEquals(sortCode, event.sortCode());
        assertNull(thrownException, "Expected no exception, but got: " + thrownException);
    }

    @Given("A Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        aValidCustomerAggregate(); // Start with valid
        // Override for violation
        emailAddress = "invalid-email";
        governmentId = ""; // Empty
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

    @Given("A Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        aValidCustomerAggregate();
        fullName = "";
        dateOfBirth = "";
    }

    @Given("A Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveBankAccounts() {
        aValidCustomerAggregate();
        hasActiveBankAccounts = true;
    }
}
