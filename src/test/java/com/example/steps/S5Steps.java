package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private String customerId;
    private String accountType;
    private BigDecimal initialDeposit;
    private String sortCode;
    private List<DomainEvent> resultEvents;
    private RuntimeException thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        String accountId = UUID.randomUUID().toString();
        aggregate = new AccountAggregate(accountId);
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        this.customerId = "customer-123";
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        this.accountType = "SAVINGS";
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        this.initialDeposit = new BigDecimal("500.00");
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        this.sortCode = "10-20-30";
    }

    // Scenario 2: Minimum Balance Violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate(UUID.randomUUID().toString());
        customerId = "cust-999";
        accountType = "CURRENT"; // Assume 1000 min
        initialDeposit = new BigDecimal("50.00"); // Violation
        sortCode = "10-20-30";
    }

    // Scenario 3: Active Status Violation (Simulated via context or command constraint)
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate(UUID.randomUUID().toString());
        // In a real OpenAccount flow, this might check if the customer is active.
        // Here we simulate the command logic failing because the context (e.g., Customer) is not Active.
        // We will trigger this by setting an invalid customer context flag in the command if we had one.
        // For this test, we assume the command factory sets a flag, or we just check the specific exception.
        // Let's assume customerId "inactive-cust" triggers this invariant failure.
        customerId = "INACTIVE_CUSTOMER_MARKER"; 
        accountType = "SAVINGS";
        initialDeposit = new BigDecimal("100.00");
        sortCode = "10-20-30";
    }

    // Scenario 4: Unique Account Number Violation
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        aggregate = new AccountAggregate(UUID.randomUUID().toString());
        // We simulate this by providing a null/empty ID generator result in the aggregate context,
        // or calling open twice on the same aggregate instance.
        // The easiest way to test this invariant for a NEW aggregate is to try to open it twice.
        customerId = "customer-123";
        accountType = "SAVINGS";
        initialDeposit = new BigDecimal("100.00");
        sortCode = "10-20-30";
        
        // Execute once to put it in OPENED state, next execution fails uniqueness/immutable check
        Command cmd = new OpenAccountCmd(aggregate.id(), customerId, accountType, initialDeposit, sortCode);
        aggregate.execute(cmd);
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        try {
            Command cmd = new OpenAccountCmd(aggregate.id(), customerId, accountType, initialDeposit, sortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (RuntimeException e) {
            thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals("account.opened", event.type());
        assertNotNull(event.occurredAt());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Check it's not just a random NPE
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }
}
