package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import com.example.domain.customer.repository.CustomerRepository;
import com.example.mocks.InMemoryCustomerRepository;

import java.util.List;
import java.util.Optional;

public class S4Steps {

    private CustomerAggregate aggregate;
    private CustomerRepository repository = new InMemoryCustomerRepository();
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    // Helper to simulate a valid enrolled customer state directly
    private void createValidEnrolledAggregate(String id, String name, String email, String govId, Instant dob, boolean hasActiveAccounts) {
        // We use reflection or package-private access if we were loading from events, 
        // but here we instantiate and mutate directly for test setup.
        // In a real scenario, we would rehydrate from events.
        aggregate = new CustomerAggregate(id);
        
        // Simulate state post-enrollment
        // NOTE: In a real application, we would load the aggregate via repository.load(id)
        // and it would apply events. For unit test speed, we construct directly.
        try {
            // We effectively set the state to 'enrolled' by executing an enroll command first
            // or by mutating protected fields if visible. 
            // To keep it clean and consistent with AggregateRoot behavior:
            aggregate.execute(new EnrollCustomerCmd(id, name, email, govId));
            aggregate.clearEvents(); // Clear enrollment events so we only inspect Delete events

            // Set fields not exposed by EnrollCustomerCmd (like dateOfBirth, activeAccounts)
            // using a separate 'Update' command or direct access (omitted for strict DDD, 
            // but assuming setters or package-private access for test fixture).
            // *Assumption*: The CustomerAggregate has been updated to support the new invariants 
            // (DateOfBirth, ActiveAccounts) since the previous story.
            
            // Since the provided CustomerAggregate in the prompt doesn't have these fields yet,
            // we assume the implementation will add them. 
            // We will use the specific methods added in the new implementation (setters or test builders).
            // For this step definition, we rely on the fact that the 'DeleteCustomerCmd' logic
            // will check these fields.
            
            // Mocking the internal state for the 'violations' scenarios:
            // We will need to update the CustomerAggregate to allow setting these for tests
            // or have a specific 'SetProfileInfo' command. 
            // Given the constraints, I will assume the Aggregate has been updated to support these checks.

        } catch (Exception e) {
            throw new RuntimeException("Failed to setup valid aggregate state", e);
        }
    }

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        // Default valid customer
        createValidEnrolledAggregate("cust-1", "John Doe", "john@example.com", "GOV-123", Instant.now(), false);
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Handled by the aggregate creation
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_DeleteCustomerCmd_command_is_executed() {
        try {
            Command cmd = new DeleteCustomerCmd(aggregate.id());
            resultingEvents = aggregate.execute(cmd);
            capturedException = null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        } catch (UnknownCommandException e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertEquals("customer.deleted", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException);
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || 
                              capturedException instanceof IllegalStateException);
    }

    // --- Specific Violation Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_that_violates_email_and_gov_id() {
        // Create a customer, but we can't easily violate internal state via execute 
        // unless we have a command to change email to invalid.
        // Assuming the new implementation adds a method to set these for testing/evolution
        // OR we rely on the fact that 'EnrollCustomerCmd' validates them, but here we are already enrolled.
        // *Assumption*: The Aggregate constructor or rehydration allows setting a state that might be invalid 
        // (e.g. legacy data), or we use a specific command. 
        // For the test to pass, the CustomerAggregate implementation must allow setting an invalid state
        // (e.g. `setEmail(null)`). 
        // I will assume a `setEmail` method or direct field access exists in the updated Aggregate.
        aggregate = new CustomerAggregate("cust-invalid");
        // Force invalid state
        // Note: In the updated code below, I add a package-private or protected setter or builder mechanism.
        aggregate.setEmail("invalid-email"); 
        aggregate.setGovernmentId(null); 
        aggregate.markEnrolled(); // Bypassing enrollment validation for the sake of testing the Delete invariant on existing data
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_that_violates_name_and_dob() {
        aggregate = new CustomerAggregate("cust-empty");
        aggregate.setFullName(null);
        aggregate.setDateOfBirth(null);
        aggregate.markEnrolled();
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_that_violates_active_accounts() {
        createValidEnrolledAggregate("cust-active", "Active User", "active@example.com", "GOV-999", Instant.now(), true);
        aggregate.setHasActiveAccounts(true);
    }
}
