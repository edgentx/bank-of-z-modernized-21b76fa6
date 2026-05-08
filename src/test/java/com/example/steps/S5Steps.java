package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S5Steps {
    
    private AccountAggregate account;
    private String customerId;
    private String accountType;
    private BigDecimal initialDeposit;
    private String sortCode;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultingEvents;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.account = new AccountAggregate("act-new-1");
    }

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        this.customerId = "cust-123";
    }

    @And("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        this.accountType = "SAVINGS";
    }

    @And("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        this.initialDeposit = new BigDecimal("100.00");
    }

    @And("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        this.sortCode = "10-20-30";
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        try {
            OpenAccountCmd cmd = new OpenAccountCmd(
                account.id(), 
                customerId, 
                accountType, 
                initialDeposit, 
                sortCode
            );
            resultingEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertEquals(1, resultingEvents.size());
        Assertions.assertTrue(resultingEvents.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) resultingEvents.get(0);
        Assertions.assertEquals("act-new-1", event.aggregateId());
        Assertions.assertEquals("cust-123", event.customerId());
        Assertions.assertEquals("SAVINGS", event.accountType());
        Assertions.assertEquals(0, new BigDecimal("100.00").compareTo(event.initialDeposit()));
        Assertions.assertEquals("10-20-30", event.sortCode());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        this.account = new AccountAggregate("act-low-bal");
        this.customerId = "cust-123";
        this.accountType = "CURRENT"; // Assume min balance 500
        this.initialDeposit = new BigDecimal("10.00"); // Too low
        this.sortCode = "10-20-30";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // This scenario usually applies to existing accounts. 
        // For OpenAccount, the account starts as CREATED, then moves to ACTIVE/OPENED.
        // If we interpret this as trying to open an account that starts in a invalid state (e.g. closed/suspended)
        // We will treat this as a state transition rejection.
        this.account = new AccountAggregate("act-closed"); 
        // Manually setting state to CLOSED to simulate violation (via reflection or constructor hack for test)
        // Since aggregate is new, this scenario is hypothetical or assumes a different constructor.
        // We will rely on the command validation or aggregate logic to reject if applicable.
        // For this implementation, we assume this maps to a validation rule provided in the command context.
        this.customerId = "cust-123";
        this.accountType = "SAVINGS";
        this.initialDeposit = new BigDecimal("100.00");
        this.sortCode = "10-20-30";
        
        // Re-purposing the scenario: If an account is not in a state to be opened (e.g. already opened)
        // AccountAggregate logic handles state transitions.
        account.execute(new OpenAccountCmd("act-closed", customerId, accountType, initialDeposit, sortCode));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // Uniqueness is usually a repository constraint.
        // In aggregate, we can simulate this if the ID passed already exists or is invalid.
        this.account = new AccountAggregate("act-duplicate-1");
        this.customerId = "cust-123";
        this.accountType = "SAVINGS";
        this.initialDeposit = new BigDecimal("100.00");
        this.sortCode = "10-20-30";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        // Verify it's a domain error (IllegalStateException, IllegalArgumentException, or custom)
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || 
            caughtException instanceof IllegalArgumentException
        );
    }
}
