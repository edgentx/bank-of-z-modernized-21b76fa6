package com.example.steps;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.model.CustomerDetailsUpdatedEvent;
import com.example.domain.customer.model.UpdateCustomerDetailsCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S3Steps {

    private CustomerAggregate customer;
    private String customerId;
    private String email;
    private String sortCode;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Clean state for each scenario if needed, though Cucumber usually creates a new instance
    public S3Steps() {}

    @Given("a valid Customer aggregate")
    public void a_valid_Customer_aggregate() {
        customerId = "cust-123";
        customer = new CustomerAggregate(customerId);
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // customerId already set
        assertNotNull(customerId);
    }

    @And("a valid emailAddress is provided")
    public void a_valid_emailAddress_is_provided() {
        email = "new.email@example.com";
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        sortCode = "12-34-56";
    }

    @When("the UpdateCustomerDetailsCmd command is executed")
    public void the_UpdateCustomerDetailsCmd_command_is_executed() {
        // Default valid details for the happy path
        String name = "John Doe";
        String dob = "1990-01-01";
        String govId = "GOV-123";
        boolean hasActiveAccounts = false;

        UpdateCustomerDetailsCmd cmd = new UpdateCustomerDetailsCmd(
                customerId, name, email, govId, dob, sortCode, hasActiveAccounts
        );

        try {
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a customer.details.updated event is emitted")
    public void a_customer_details_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof CustomerDetailsUpdatedEvent);

        CustomerDetailsUpdatedEvent event = (CustomerDetailsUpdatedEvent) resultEvents.get(0);
        assertEquals(email, event.emailAddress());
        assertEquals(sortCode, event.sortCode());
        assertEquals(customerId, event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a Customer aggregate that violates: A customer must have a valid, unique email address and government-issued ID.")
    public void a_customer_aggregate_with_invalid_email_and_gov_id() {
        customer = new CustomerAggregate("cust-violate-id");
    }

    @Given("a Customer aggregate that violates: Customer name and date of birth cannot be empty.")
    public void a_customer_aggregate_with_empty_name_dob() {
        customer = new CustomerAggregate("cust-violate-name");
    }

    @Given("a Customer aggregate that violates: A customer cannot be deleted if they own active bank accounts.")
    public void a_customer_aggregate_with_active_accounts() {
        customer = new CustomerAggregate("cust-violate-acct");
    }

    @When("the UpdateCustomerDetailsCmd command is executed with violations")
    public void the_UpdateCustomerDetailsCmd_command_is_executed_with_violations() {
        // Construct command to trigger specific invariants based on the scenario context
        String badEmail = "invalid-email"; // Triggers Invalid Email/ID invariant
        String emptyName = ""; // Triggers Empty Name invariant
        boolean hasAccounts = true; // Triggers Active Accounts invariant

        // We construct a single command execution block. 
        // Because the 'When' step is shared, we rely on the specific setup in 'Given' to imply what is being tested, 
        // but to be precise in Java code, we execute one specific logic path.
        // However, the scenarios are distinct. We will check the exception type in Then.
        
        // To handle multiple scenarios with one When method, we can check the state of the aggregate 
        // or simply execute a command that violates one of the rules.
        // Given the scenarios, we will execute a command designed to trigger a specific failure 
        // based on the aggregate state/context, but here the aggregate itself is stateless regarding the *new* data 
        // until the command runs.
        
        // Let's look at the Scenario names:
        // 1. Invalid email/gov id -> use badEmail
        // 2. Empty name/dob -> use emptyName
        // 3. Active accounts -> use hasAccounts=true

        // Since the 'When' step is identical in text, we can execute logic that covers the intent 
        // or simply pass the violation based on the last setup.
        // For robust BDD, we often parameterize or use a shared context object. 
        // Here we will check the customerId to simulate the specific violation path 
        // (simple mock behavior).

        UpdateCustomerDetailsCmd cmd;
        
        if (customer.id().equals("cust-violate-id")) {
             cmd = new UpdateCustomerDetailsCmd(customer.id(), "Name", "bad-email", "Gov", "1990-01-01", "Sort", false);
        } else if (customer.id().equals("cust-violate-name")) {
             cmd = new UpdateCustomerDetailsCmd(customer.id(), "", "email@test.com", "Gov", "1990-01-01", "Sort", false);
        } else if (customer.id().equals("cust-violate-acct")) {
             cmd = new UpdateCustomerDetailsCmd(customer.id(), "Name", "email@test.com", "Gov", "1990-01-01", "Sort", true);
        } else {
             // Should not happen given the Givens
             cmd = new UpdateCustomerDetailsCmd(customer.id(), "Name", "email@test.com", "Gov", "1990-01-01", "Sort", false);
        }

        try {
            resultEvents = customer.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Checking if it's a Runtime exception (IllegalArgument or IllegalState)
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
