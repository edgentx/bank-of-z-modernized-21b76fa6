package com.example.steps;

import com.example.domain.customer.command.UpdateCustomerDetailsCmd;
import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S3Steps {

    private CustomerAggregate customer;
    private UpdateCustomerDetailsCmd cmd;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // Scenarios setup

    @Given("a valid Customer aggregate")
    public void a_valid_customer_aggregate() {
        customer = new CustomerAggregate("cust-123");
        // Pre-enroll or setup valid state if necessary, though S-3 implies update on existing.
        // Assuming valid base state for positive flow.
        customer.setHasActiveAccounts(false); 
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_that_violates_email_and_id() {
        // In a real repository scenario, uniqueness is checked by the repo.
        // Here we simulate the condition by preparing a command with invalid data (caught in aggregate)
        // Or we set the aggregate state such that it would fail checks.
        // The prompt specifies this invariant violation in the context of the command execution.
        customer = new CustomerAggregate("cust-123");
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_that_violates_name_dob() {
        customer = new CustomerAggregate("cust-123");
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_that_violates_active_accounts() {
        customer = new CustomerAggregate("cust-123");
        customer.setHasActiveAccounts(true);
    }

    // Inputs

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Handled in command construction below
    }

    @And("a valid emailAddress is provided")
    public void a_valid_email_address_is_provided() {
        // Handled in command construction below
    }

    @And("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Handled in command construction below
    }

    // Action

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_update_customer_details_cmd_command_is_executed() {
        try {
            // Determine which scenario we are in based on the Given setup
            String testName = "valid";
            if (customer.isHasActiveAccountsFlagSetForTest()) { // Helper check
                testName = "active_accounts";
            } // Heuristic for other scenarios or context injection could be used.
            // Since Cucumber isolates steps, we can infer payload from specific Scenario context
            // But Cucumber steps share state. We need a way to differentiate.
            // Standard approach: Use specific Givens to set specific state flags.
            
            // Payload construction logic mapping to the scenarios:
            // 1. Success
            // 2. Invalid Email
            // 3. Empty Name/DOB
            // 4. Active Accounts (state check on aggregate)
            
            // We will derive the command content from the "violates" state or valid defaults.
            // For simplicity in this generated file, we check the aggregate state flags set in Given.
            
            String id = "cust-123";
            String email = "test@example.com";
            String sort = "123456";
            String name = "John Doe";
            String dob = "1990-01-01";

            // Scenario 2: Violates Email
            // We need a signal. Let's assume if it's not the active account scenario, we might be testing others.
            // However, step definitions don't know the scenario title.
            // We will use specific Givens for invalid inputs in a real robust suite, but per AC text:
            // "Given a Customer aggregate that violates: ... valid email"
            // This implies the *aggregate* state or the *inputs* violate it.
            // Let's assume for Scenario 2, the command has invalid email.
            // We'll infer this by not setting the active accounts flag but checking a flag for "invalid email test".
            // To keep it simple and working with the provided AC text:
            
            // We'll rely on specific string matching in the Given for better reliability, but here is a generic dispatch.
            // Actually, the provided "violates" Givens don't set specific *inputs*, they set the aggregate state.
            // EXCEPT for the name/dob and email ones, which imply the inputs are bad.
            // Let's assume the standard inputs are valid, and we modify them if a specific flag is set.
            
            // Refining logic based on standard Cucumber patterns:
            // Usually: Given an invalid email "bad-email"
            // Here: Given an aggregate that violates... generic.
            // I will map specific scenarios by checking class-level flags set in Given.
            
            if (this.isInvalidEmailScenario) {
                email = "invalid-email";
            }
            if (this.isEmptyNameScenario) {
                name = "";
            }
            if (this.isEmptyDobScenario) {
                dob = "";
            }
            // Active accounts is already a state on the aggregate.

            cmd = new UpdateCustomerDetailsCmd(id, email, sort, name, dob);
            resultingEvents = customer.execute(cmd);

        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Outcomes

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof CustomerDetailsUpdatedEvent);
        
        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultingEvents.get(0);
        assertEquals("customer.details.updated", event.type());
        assertEquals("cust-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

    // Test Helpers / Flags (Set in Given to control When behavior)
    private boolean isInvalidEmailScenario = false;
    private boolean isEmptyNameScenario = false;
    private boolean isEmptyDobScenario = false;

    // Flag setters for Given steps to communicate with When
    public void markInvalidEmailScenario() { this.isInvalidEmailScenario = true; }
    public void markEmptyNameScenario() { this.isEmptyNameScenario = true; }
    public void markEmptyDobScenario() { this.isEmptyDobScenario = true; }

    // --- Concrete Given implementations for the specific AC text ---
    
    // Re-mapping the vague Givens to specific flags
    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void setup_violates_email_id() {
        a_customer_aggregate_that_violates_email_and_id();
        markInvalidEmailScenario(); // Signal to use bad email
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void setup_violates_name_dob() {
        a_customer_aggregate_that_violates_name_dob();
        markEmptyNameScenario(); // Signal to use empty name
        // Note: The AC implies both violations. We pick one for the exception.
    }

    // Helper method to check if active accounts flag was set (since we can't access private field easily without reflection)
    // In a real test, we might set a public 'scenario' enum.
    // Here, we assume if the 'active accounts' Given was called, the state is set.
    // We need a way to detect it. Adding a simple check flag.
    private boolean activeAccountsScenario = false;
    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void setup_violates_active_accounts() {
        a_customer_aggregate_that_violates_active_accounts();
        this.activeAccountsScenario = true;
    }

    // Dirty hack for the lack of context in generated steps:
    // We add a method to CustomerAggregate in test scope or check a property?
    // No, we can use the knowledge that we set it to true.
    
    // Let's refine the dispatch in 'When' to check activeAccountsScenario first.
    
    // Extension of CustomerAggregate for testing (or package-private access)
    // We'll assume package-private access to setHasActiveAccounts works.
    
    // Internal helper for the steps
    private boolean isHasActiveAccountsFlagSetForTest() {
        return activeAccountsScenario; 
    }
}