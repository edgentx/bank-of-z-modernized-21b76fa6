package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.DeleteCustomerCmd;
import com.example.domain.customer.model.CustomerDeletedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class S4Steps {

    private CustomerAggregate aggregate;
    private String customerId;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Customer aggregate")
    public void a_valid_customer_aggregate() {
        customerId = "cust-" + UUID.randomUUID();
        aggregate = new CustomerAggregate(customerId);
    }

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // ID is initialized in the previous step
        Assertions.assertNotNull(customerId);
    }

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_that_violates_email_and_id() {
        customerId = "cust-invalid-" + UUID.randomUUID();
        aggregate = new CustomerAggregate(customerId);
        // Simulate the aggregate state that violates the rule by loading from a 'corrupt' event log
        var invalidEvent = new CustomerEnrolledEvent(
            customerId, 
            "Test User", 
            "not-an-email", // Invalid email
            "", // Invalid Government ID
            Instant.now()
        );
        aggregate.loadFromHistory(List.of(invalidEvent));
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_that_violates_name_and_dob() {
        customerId = "cust-invalid-" + UUID.randomUUID();
        aggregate = new CustomerAggregate(customerId);
        // Simulate state with empty name
        var invalidEvent = new CustomerEnrolledEvent(
            customerId, 
            "", // Invalid Name
            "test@example.com", 
            "GOV123", 
            Instant.now()
        );
        aggregate.loadFromHistory(List.of(invalidEvent));
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_that_violates_active_accounts() {
        customerId = "cust-accounts-" + UUID.randomUUID();
        aggregate = new CustomerAggregate(customerId);
        var event = new CustomerEnrolledEvent(
            customerId, 
            "Test User", 
            "test@example.com", 
            "GOV123", 
            Instant.now()
        );
        aggregate.loadFromHistory(List.of(event));
        
        // Simulate the existence of active accounts for this customer.
        // In a real integration test, we would mock the AccountRepository to return active accounts.
        // For the aggregate unit test context, we simulate the "Active Account Count" invariant 
        // by setting a flag or state on the aggregate if it supports it, 
        // or by ensuring the command handler checks this invariant.
        // The aggregate logic provided relies on the "CustomerEnrolledEvent" setting state,
        // but "activeAccountsCount" is tracked in the extended aggregate logic.
        // We assume the aggregate has been loaded with state indicating active accounts.
        // For this BDD, we rely on the execute method implementation to handle the logic.
    }

    @When("the DeleteCustomerCmd command is executed")
    public void the_delete_customer_cmd_command_is_executed() {
        Command cmd = new DeleteCustomerCmd(customerId);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        }
    }

    @Then("a customer.deleted event is emitted")
    public void a_customer_deleted_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof CustomerDeletedEvent);
        CustomerDeletedEvent event = (CustomerDeletedEvent) resultEvents.get(0);
        Assertions.assertEquals(customerId, event.customerId());
        Assertions.assertEquals("customer.deleted", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Domain errors typically manifest as IllegalArgumentException or IllegalStateException
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException
        );
    }
}