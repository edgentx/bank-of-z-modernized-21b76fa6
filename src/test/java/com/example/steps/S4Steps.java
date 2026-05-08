package com.example.steps;

import com.example.domain.customer.model.*;
import com.example.domain.customer.repository.CustomerRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.mocks.InMemoryCustomerRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S4Steps {

    private CustomerAggregate aggregate;
    private CustomerRepository repository = new InMemoryCustomerRepository();
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_customer_aggregate() {
        aggregate = new CustomerAggregate("cust-123");
        // Enroll the customer first to make it valid
        Command enrollCmd = new EnrollCustomerCmd("cust-123", "John Doe", "john@example.com", "GOV-ID-123");
        aggregate.execute(enrollCmd);
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Id is already set in previous step
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_delete_customer_cmd_command_is_executed() {
        Command deleteCmd = new DeleteCustomerCmd("cust-123", false);
        try {
            resultEvents = aggregate.execute(deleteCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDeletedEvent);
        assertEquals("customer.deleted", resultEvents.get(0).type());
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_that_violates_email_and_id() {
        aggregate = new CustomerAggregate("cust-invalid");
        // Enroll with bad data to satisfy the internal state, or manipulate state if constructor allowed.
        // Here we rely on the Enroll logic to set the state, but we want to test Delete invariant.
        // The prompt says "Given a Customer aggregate that violates...".
        // Let's create a valid one then modify the 'uncommitted' logic, or just instantiate.
        // To strictly test the Delete invariant about 'valid email', the aggregate must check it at delete time.
        Command enrollCmd = new EnrollCustomerCmd("cust-invalid", "Jane", "invalid-email", "");
        aggregate.execute(enrollCmd);
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_that_violates_name_and_dob() {
        aggregate = new CustomerAggregate("cust-invalid-name");
        Command enrollCmd = new EnrollCustomerCmd("cust-invalid-name", "", "test@test.com", "GOV-ID");
        aggregate.execute(enrollCmd);
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_that_violates_active_accounts() {
        aggregate = new CustomerAggregate("cust-active");
        Command enrollCmd = new EnrollCustomerCmd("cust-active", "Active User", "active@example.com", "GOV-ID");
        aggregate.execute(enrollCmd);
        // The delete command must flag that accounts exist. Since this is an aggregate unit test 
        // and we don't have a real Account repo lookup inside the aggregate, 
        // we simulate this via a flag in the command or a state on the aggregate if it tracked accounts.
        // The command pattern in the prompt says "Marks ... if they have no active accounts".
        // This implies the validation might happen externally OR the command carries the info.
        // The prompt implies "Aggregate... enforce invariants".
        // Let's assume the Command carries a boolean flag `hasActiveAccounts` derived from a service.
    }

    @When("the DeleteCustomerCmd command is executed for invalid scenarios")
    public void the_delete_customer_cmd_command_is_executed_invalid() {
        // We assume the validation happens inside execute().
        // For the active accounts case, we pass true to the command constructor.
        String id = aggregate.id();
        boolean hasActive = id.equals("cust-active");
        Command deleteCmd = new DeleteCustomerCmd(id, hasActive);
        
        try {
            resultEvents = aggregate.execute(deleteCmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
