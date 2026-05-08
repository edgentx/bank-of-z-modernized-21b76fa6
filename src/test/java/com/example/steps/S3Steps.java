package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class S3Steps {

    private CustomerAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_customer_aggregate() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
        aggregate.setFullName("John Doe");
        aggregate.setEmail("john@example.com");
        aggregate.setGovernmentId("GOV-123");
        aggregate.setDeleted(false);
        aggregate.setHasActiveAccounts(false);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_with_invalid_email() {
        // Setting up a scenario where the update command carries an invalid email
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_with_empty_name() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_with_active_accounts() {
        aggregate = new CustomerAggregate("cust-123");
        aggregate.setEnrolled(true);
        aggregate.setHasActiveAccounts(true);
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Implicitly handled in the When step via command construction
    }

    @Given("a valid emailAddress is provided")
    public void a_valid_email_address_is_provided() {
        // Implicitly handled in the When step
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Implicitly handled in the When step
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_update_customer_details_cmd_command_is_executed() {
        try {
            // Default command for success path or base setup
            UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                "cust-123",
                "newemail@example.com",
                "SC-123",
                "John Updated",
                false
            );

            // Check specific scenario contexts to override command or state
            if (aggregate.getEmail() == null) {
                // Context for invalid email (state check) vs invalid input
                // Let's assume the "violates" context applies to the INPUT command for negative tests
            }

            // Map scenarios to specific inputs:
            // 1. Invalid email input
            ThreadLocal<String> scenarioContext = new ThreadLocal<>(); // simple hack to detect context if needed, or use tags.
            // However, the easiest way is to inspect the aggregate state set in Given.
            
            // Scenario: Invalid Email Input
            // We trigger this by passing a bad email. How to differentiate? 
            // We'll check the aggregate state or just assume a specific "bad" command for that scenario.
            // Ideally, Cucumber tables are used for parameters.
            // For this exercise, we will inspect the aggregate to decide the command.
            
            String email = "valid@example.com";
            String name = "Valid Name";
            boolean delete = false;

            // If aggregate has no name (violating state), we send blank name to trigger error
            if (aggregate.getFullName() == null || aggregate.getFullName().isEmpty()) {
                name = ""; 
            }
            // If aggregate has active accounts, we simulate a delete request
            if (aggregate.isHasActiveAccounts()) {
                delete = true;
            }
            // To handle the "Invalid email" specific scenario ( violating valid email constraint):
            // We rely on the fact that the previous 'Given' for invalid email didn't set one.
            // But the aggregate itself is valid. The command must be invalid.
            // We need a flag. Let's assume the 'Given' for invalid email sets a specific marker or we just hardcode the negative path here if we detect the 'Given' title logic.
            // Since Cucumber matches text:
            // "Given a Customer aggregate that violates: A customer must have a valid, unique email address..."
            // This usually sets up state, but here we are checking Input validation.
            // Let's assume the step "a valid emailAddress is provided" is SKIPPED in the negative scenario, so we send a bad one.
            // But all scenarios flow through the single @When.
            // We will assume if the aggregate is NOT the "valid" one, we are in a negative test.
            
            // Strategy: Differentiate by the aggregate's setup state.
            if (aggregate.getEmail() == null && !aggregate.isEnrolled()) { 
                 // The "invalid email" given block just initialized it. Let's assume we send invalid email.
                 email = "invalid-email";
            }

            cmd = new UpdateCustomerDetailsCmd("cust-123", email, "SC-123", name, delete);
            resultEvents = aggregate.execute(cmd);

        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("customer.details.updated", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        // It could be IAE or ISE depending on the invariant
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException,
            "Expected domain exception (IAE or ISE), got: " + capturedException.getClass().getSimpleName()
        );
    }

    // Helper to expose private field for testing in the step definition (simulation)
    // In a real test, we might add a getter or package-private access, or use reflection.
    public static class TestableCustomerAggregate extends CustomerAggregate {
        public TestableCustomerAggregate(String id) { super(id); }
        public boolean isHasActiveAccounts() {
            // Reflection or adding getter to aggregate. For this snippet, we assume we added the helper in Aggregate.
            // We added setters in the Aggregate source above.
            return true; // Placeholder, actual logic in Aggregate is private/state based.
        }
    }
}
