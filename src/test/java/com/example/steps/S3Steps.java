package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private UpdateCustomerDetailsCmd cmd;
    private Exception caughtException;

    // Helper to setup a valid base customer
    private void setupValidCustomer() {
        customer = new CustomerAggregate("cust-123");
        customer.enrollDirectly("cust-123", "John Doe", "john.doe@example.com", "GOV123");
    }

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        setupValidCustomer();
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId is handled in the When step construction
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        // emailAddress is handled in the When step construction
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // sortCode is handled in the When step construction
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // Scenario 1 defaults
            String fullName = "John Doe";
            String email = "john.doe@example.com";
            String govId = "GOV123";
            String dob = "1990-01-01";
            String sortCode = "123456";

            cmd = new UpdateCustomerDetailsCmd(customer.id(), fullName, email, govId, dob, sortCode);
            customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertFalse(customer.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        assertEquals("customer.details.updated", customer.uncommittedEvents().get(0).type());
    }

    // --- Negative Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailAndGovId() {
        setupValidCustomer();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameAndDob() {
        setupValidCustomer();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        setupValidCustomer();
        customer.setHasActiveAccounts(true);
    }

    // We can reuse the When clause, but we need to inject bad data based on the Given context.
    // Since Cucumber executes steps sequentially, we can assume context or just overload the When behavior.
    // However, standard practice is to use specific Whens or context injection.
    // Given the constraints, I will assume the 'When' step needs to be flexible or I will add specific Whens for the negative cases.
    // To be safe and compile successfully with the single When defined in the feature, I'll update the When implementation to handle context.
    
    // Actually, looking at the feature, the 'When' line is identical for all scenarios. 
    // I will assume the 'Given' steps setup the state, and the 'When' step implementation needs to be smart enough
    // OR (more likely for this exercise) I will check the state of the aggregate in the When step to decide what command to send.
    // BUT, the Command parameters are what causes the error. 
    // Let's look at the specific error messages to drive the command data.

    // To resolve this without over-engineering the step logic:
    // I will implement the When step to check specific conditions or use a default 'bad' command if the aggregate is in a specific 'violation' state.
    
    // Scenario 2: Bad Email/GovId
    // Scenario 3: Bad Name/Dob
    // Scenario 4: Active Accounts (This is an aggregate state check, not a command field check).

    // Let's refine the When step logic to handle these cases based on the setup state.
    
    public static class ScenarioContext {
        public static String scenarioType = "";
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void setupInvalidEmailGovIdScenario() {
        ScenarioContext.scenarioType = "INVALID_EMAIL_GOV";
        setupValidCustomer();
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void setupInvalidNameDobScenario() {
        ScenarioContext.scenarioType = "INVALID_NAME_DOB";
        setupValidCustomer();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void setupActiveAccountsScenario() {
        ScenarioContext.scenarioType = "ACTIVE_ACCOUNTS";
        setupValidCustomer();
        customer.setHasActiveAccounts(true);
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void executeCommand() {
        try {
            String id = customer.id();
            String name = "John Doe";
            String email = "john.doe@example.com";
            String govId = "GOV123";
            String dob = "1990-01-01";
            String sortCode = "123456";

            if ("INVALID_EMAIL_GOV".equals(ScenarioContext.scenarioType)) {
                email = "invalid-email"; // triggers domain error
            } else if ("INVALID_NAME_DOB".equals(ScenarioContext.scenarioType)) {
                name = ""; // triggers domain error
                dob = "";
            }
            // ACTIVE_ACCOUNTS relies on aggregate state, set in Given

            cmd = new UpdateCustomerDetailsCmd(id, name, email, govId, dob, sortCode);
            customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        } finally {
            ScenarioContext.scenarioType = ""; // reset
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected a domain exception");
        assertTrue(caughtException instanceof DomainException, "Expected DomainException");
    }
}
