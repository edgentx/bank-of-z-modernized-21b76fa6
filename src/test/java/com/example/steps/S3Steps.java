package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate aggregate;
    private String customerId;
    private String emailAddress;
    private String sortCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String governmentId;
    private boolean hasActiveAccounts;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        customerId = "cust-123";
        aggregate = new CustomerAggregate(customerId);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_that_violates_email_and_id() {
        customerId = "cust-invalid";
        aggregate = new CustomerAggregate(customerId);
        emailAddress = "invalid-email"; // Invalid email
        governmentId = ""; // Empty ID
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_that_violates_name_and_dob() {
        customerId = "cust-missing-info";
        aggregate = new CustomerAggregate(customerId);
        fullName = ""; // Empty name
        dateOfBirth = null; // Empty DOB
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_that_violates_active_accounts() {
        customerId = "cust-active-accounts";
        aggregate = new CustomerAggregate(customerId);
        hasActiveAccounts = true;
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        customerId = "cust-123";
    }

    @And("a valid emailAddress is provided")
    public void a_valid_emailAddress_is_provided() {
        emailAddress = "test@example.com";
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        sortCode = "10-20-30";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_UpdateCustomerDetailsCmd_command_is_executed() {
        try {
            // Default values for fields not explicitly set in "Given" steps if they are null
            // This ensures the command is fully populated for the positive case
            String cmdFullName = (fullName != null) ? fullName : "John Doe";
            String cmdEmail = (emailAddress != null) ? emailAddress : "default@example.com";
            String cmdSortCode = (sortCode != null) ? sortCode : "00-00-00";
            String cmdGovId = (governmentId != null) ? governmentId : "GOV123";
            LocalDate cmdDob = (dateOfBirth != null) ? dateOfBirth : LocalDate.of(1990, 1, 1);
            boolean cmdActive = hasActiveAccounts;

            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                customerId, cmdFullName, cmdEmail, cmdSortCode, cmdGovId, cmdDob, cmdActive
            );

            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("customer.details.updated", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertNull(resultEvents);
    }

    // Reset state between scenarios if necessary, though Cucumber typically instantiates a new step class
}
