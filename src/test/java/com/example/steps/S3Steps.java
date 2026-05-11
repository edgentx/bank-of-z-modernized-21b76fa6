package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private String customerId;
    private String email;
    private String sortCode;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void aValidCustomerAggregate() {
        customerId = "cust-123";
        // Simulate an enrolled customer by setting internal state directly for test setup
        // (In a real scenario we might issue an EnrollCustomerCmd, but here we focus on Update)
        customer = new CustomerAggregate(customerId);
        customer.setMemento("Old Name", "old@example.com", "10-20-30", "GOV123", Instant.now().minusSeconds(1000), true, false);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // customerId already initialized
        assertNotNull(customerId);
    }

    @And("a valid emailAddress is provided")
    public void aValidEmailAddressIsProvided() {
        email = "new.valid@example.com";
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        sortCode = "10-20-30";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted() {
        try {
            // Default valid values for the successful scenario
            String name = "New Name";
            String govId = "GOV123";
            Instant dob = Instant.now().minusSeconds(100000000);
            
            // Check if we are in a failure scenario context by checking system state or variables
            // However, Cucumber steps are stateless between scenarios usually. 
            // We construct the command based on the Given state.
            
            Command cmd = new UpdateCustomerDetailsCmd(customerId, name, email, sortCode, govId, dob, false);
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void aCustomerDetailsUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultEvents.get(0);
        assertEquals("customer.details.updated", event.type());
        assertEquals(customerId, event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThat violatesEmailOrId() {
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        customer.setMemento("Name", "old@example.com", "10-20-30", "GOV123", Instant.now(), true, false);
        // We will trigger the violation by passing a bad email in the When step indirectly via variables
        // or checking specific violation flags. Let's set up the 'bad' variables here.
        email = "invalid-email-format"; // Invalid email
        sortCode = "10-20-30";
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameOrDob() {
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        customer.setMemento("Name", "old@example.com", "10-20-30", "GOV123", Instant.now(), true, false);
        // Set variables to invalid values
        email = "valid@example.com";
        sortCode = "10-20-30";
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts() {
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        customer.setMemento("Name", "old@example.com", "10-20-30", "GOV123", Instant.now(), true, false);
        // Setup for valid details, but we will flag hasActiveAccounts = true in the command
        email = "valid@example.com";
        sortCode = "10-20-30";
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // The error should be either IllegalArgumentException or IllegalStateException
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    // --- Overriding When for specific error contexts based on state ---
    
    // In Cucumber Java, we can reuse the @When step, but the context depends on the @Given.
    // To handle the specific data for the negative tests without branching the @When step heavily,
    // we can set specific flags or check the 'email' variable set in the Given steps.
    
    // However, the current @When implementation uses the 'email' field.
    // For "Customer name... empty", we need to send a null/blank name.
    // For "Active accounts", we need to send true for hasActiveAccounts.
    
    // We'll adjust the @When step to be context-aware or define specific steps if needed.
    // To keep it simple and clean within one file, we can add helper methods or conditionals.
    
    // Refined @When logic for the error cases:
    // We can use a simple check on the 'email' variable to determine which failure mode we are in,
    // or better, specific Given steps setup the context, and we can read from it.
    
    // Let's refine the @When implementation to handle these:
    
    // NOTE: Cucumber matches the first step definition found. The generic @When above runs first.
    // We should replace the generic @When with specific logic or hook into it.
    // To ensure robust testing, I will redefine the @When to be smarter or use specific hooks.
    
    // Since we cannot change the 'language' of the prompt's feature file, we assume the generic @When runs.
    // To fix the logic for the negative tests, we can inspect the state set by the Given methods.
    
    // Let's redefine the @When step here to handle all scenarios correctly.
    // (Ideally, we would have separate steps for "When valid" and "When invalid", but we must stick to the Gherkin).

    // I will update the @When method to switch based on the context provided by the Given steps.
    // Since Java doesn't have 'scenario context' out of the box, we use instance variables.
    
    // Updated logic:
    // 1. If email == "invalid-email-format" -> triggers invalid email.
    // 2. If we are in the "Name/DOB empty" scenario -> we need a flag. Let's use a specific string marker for name or a flag.
    //    But the Gherkin for Name/DOB doesn't specify the input values, just the violation.
    //    We can check a boolean flag set in the Given method.
    // 3. If we are in the "Active accounts" scenario -> we check a boolean flag.

    private boolean isNameDobViolation = false;
    private boolean isActiveAccountsViolation = false;

    // Redefine Given methods to set flags
    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void aCustomerAggregateThatViolatesNameOrDob_SetFlag() {
        isNameDobViolation = true;
        isActiveAccountsViolation = false;
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        customer.setMemento("Name", "old@example.com", "10-20-30", "GOV123", Instant.now(), true, false);
        email = "valid@example.com"; // valid email
        sortCode = "10-20-30";
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void aCustomerAggregateThatViolatesActiveAccounts_SetFlag() {
        isNameDobViolation = false;
        isActiveAccountsViolation = true;
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        customer.setMemento("Name", "old@example.com", "10-20-30", "GOV123", Instant.now(), true, false);
        email = "valid@example.com";
        sortCode = "10-20-30";
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void aCustomerAggregateThatViolatesEmailOrId_SetFlag() {
        isNameDobViolation = false;
        isActiveAccountsViolation = false;
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
        customer.setMemento("Name", "old@example.com", "10-20-30", "GOV123", Instant.now(), true, false);
        email = "bad-email"; // Triggers email violation
        sortCode = "10-20-30";
    }

    // The single @When implementation handling all paths
    @When("the UpdateCustomerDetailsCmd command is executed")
    public void theUpdateCustomerDetailsCmdCommandIsExecuted_ContextAware() {
        try {
            String name = "New Name";
            if (isNameDobViolation) name = ""; // Trigger empty name
            
            Instant dob = Instant.now().minusSeconds(100000000);
            if (isNameDobViolation) dob = null; // Trigger empty dob
            
            boolean hasActive = isActiveAccountsViolation; // Trigger active accounts check

            Command cmd = new UpdateCustomerDetailsCmd(customerId, name, email, sortCode, "GOV123", dob, hasActive);
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

}
