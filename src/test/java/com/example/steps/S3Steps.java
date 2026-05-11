package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.EnrollCustomerCmd;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customer = new CustomerAggregate("cust-123");
        // Enroll first to ensure a valid base state
        EnrollCustomerCmd enrollCmd = new EnrollCustomerCmd("cust-123", "John Doe", "john.doe@example.com", "GOV-ID-001");
        customer.execute(enrollCmd);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        customer = new CustomerAggregate("cust-invalid");
        // Enrolling the aggregate first so it exists
        EnrollCustomerCmd enrollCmd = new EnrollCustomerCmd("cust-invalid", "Invalid User", "invalid.user@example.com", "GOV-ID-002");
        customer.execute(enrollCmd);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        customer = new CustomerAggregate("cust-missing-info");
        EnrollCustomerCmd enrollCmd = new EnrollCustomerCmd("cust-missing-info", "Existing User", "existing@example.com", "GOV-ID-003");
        customer.execute(enrollCmd);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customer = new CustomerAggregate("cust-active");
        EnrollCustomerCmd enrollCmd = new EnrollCustomerCmd("cust-active", "Active User", "active@example.com", "GOV-ID-004");
        customer.execute(enrollCmd);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Ids are handled in the command construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // Emails are handled in the command construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Sort code is handled in the command construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        // Scenario 1: Success case
        if ("cust-123".equals(customer.id())) {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                    "cust-123",
                    "John Doe Updated",
                    "john.updated@example.com",
                    "GOV-ID-001",
                    "1990-01-01",
                    "10-20-30",
                    false
            );
            try {
                resultEvents = customer.execute(cmd);
            } catch (Exception e) {
                caughtException = e;
            }
        }
        // Scenario 2: Invalid Email/GovId
        else if ("cust-invalid".equals(customer.id())) {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                    "cust-invalid",
                    "Invalid User",
                    "not-an-email", // Invalid email
                    null,           // Missing Gov ID
                    "1990-01-01",
                    "10-20-30",
                    false
            );
            try {
                resultEvents = customer.execute(cmd);
            } catch (Exception e) {
                caughtException = e;
            }
        }
        // Scenario 3: Empty Name/DOB
        else if ("cust-missing-info".equals(customer.id())) {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                    "cust-missing-info",
                    "",   // Empty name
                    "existing@example.com",
                    "GOV-ID-003",
                    "",   // Empty DOB
                    "10-20-30",
                    false
            );
            try {
                resultEvents = customer.execute(cmd);
            } catch (Exception e) {
                caughtException = e;
            }
        }
        // Scenario 4: Active Accounts (uses the hasActiveAccounts flag)
        else if ("cust-active".equals(customer.id())) {
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                    "cust-active",
                    "Active User",
                    "active@example.com",
                    "GOV-ID-004",
                    "1990-01-01",
                    "10-20-30",
                    true // Has active accounts - should block update per invariant requirements
            );
            try {
                resultEvents = customer.execute(cmd);
            } catch (Exception e) {
                caughtException = e;
            }
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultEvents.get(0);
        assertEquals("customer.details.updated", event.type());
        assertEquals("John Doe Updated", event.fullName());
        assertEquals("john.updated@example.com", event.emailAddress());
        assertEquals("10-20-30", event.sortCode());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Check for specific invariant failures based on the scenario setup
        assertTrue(
                caughtException.getMessage().contains("A customer must have a valid, unique email address and government-issued ID") ||
                caughtException.getMessage().contains("Customer name and date of birth cannot be empty") ||
                caughtException.getMessage().contains("A customer cannot be deleted if they own active bank accounts")
        );
    }
}