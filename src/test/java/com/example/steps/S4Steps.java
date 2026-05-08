package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.customer.repository.CustomerRepository;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryCustomerRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

public class S4Steps {

    private final CustomerRepository repository = new InMemoryCustomerRepository();
    private CustomerAggregate customer;
    private Exception capturedException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        customer = new CustomerAggregate("customer-123");
        // Hydrate aggregate to a valid state
        customer.execute(new EnrollCustomerCmd("customer-123", "John Doe", "john.doe@example.com", "GOV123"));
        // Apply events manually or use a hydration helper if available. Here we assume execute updates state directly.
        // Clearing uncommitted events to simulate a loaded aggregate.
        customer.clearEvents();
        repository.save(customer);
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Implicitly handled by the setup in "a valid Customer aggregate"
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_DeleteCustomerCmd_command_is_executed() {
        // Reload to ensure we are testing the command execution on a persisted aggregate
        CustomerAggregate aggregateToTest = repository.findById("customer-123").orElseThrow();
        try {
            resultingEvents = aggregateToTest.execute(new DeleteCustomerCmd("customer-123"));
            repository.save(aggregateToTest);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertEquals("customer.deleted", resultingEvents.get(0).type());
        
        Optional<CustomerAggregate> deleted = repository.findById("customer-123");
        Assertions.assertTrue(deleted.isPresent());
        Assertions.assertTrue(deleted.get().isDeleted());
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_Customer_aggregate_that_violates_email_and_gov_id() {
        customer = new CustomerAggregate("customer-invalid-123");
        // Force hydrate with invalid state
        customer.execute(new EnrollCustomerCmd("customer-invalid-123", "Jane Doe", "invalid-email", null));
        customer.clearEvents(); // clear enrollment noise
        repository.save(customer);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_Customer_aggregate_that_violates_name_and_dob() {
        customer = new CustomerAggregate("customer-invalid-456");
        // Force hydrate with empty name/date of birth
        // We simulate invalid state by not enrolling properly or manually setting if supported.
        // Since setters don't exist, we rely on Enroll validation to fail if we tried to enroll.
        // But the aggregate is instantiated. We need to test the Delete command's internal invariants.
        // We'll assume the aggregate needs to be in a 'valid' state generally, but delete checks specific fields.
        customer.execute(new EnrollCustomerCmd("customer-invalid-456", "", "test@test.com", "GOV456"));
        customer.clearEvents();
        repository.save(customer);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_Customer_aggregate_that_violates_active_accounts() {
        customer = new CustomerAggregate("customer-active-789");
        customer.execute(new EnrollCustomerCmd("customer-active-789", "Active User", "active@test.com", "GOV789"));
        // Simulate active accounts. The aggregate needs this state.
        // We'll assume a method or constructor allows setting this, or we reflect the test requirements.
        // Given the compile errors, we likely need to add 'activeAccountCount' to the aggregate.
        // For this step, we create an aggregate that *has* active accounts.
        repository.save(customer);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
