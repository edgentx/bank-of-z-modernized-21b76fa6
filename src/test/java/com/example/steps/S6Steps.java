package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        account = new AccountAggregate("acc-123");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setAccountNumber("123456");
        account.setType(AccountAggregate.AccountType.CHECKING);
        account.setBalance(new BigDecimal("1000.00"));
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // No-op for scenario flow, captured in command construction in 'When'
    }

    @And("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // No-op for scenario flow
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(
            "123456", 
            AccountAggregate.AccountStatus.FROZEN, 
            null, 
            null
        );
        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals(AccountAggregate.AccountStatus.FROZEN, event.newStatus());
        assertEquals("acc-123", event.aggregateId());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        account = new AccountAggregate("acc-low-balance");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setAccountNumber("999999");
        account.setType(AccountAggregate.AccountType.SAVINGS); // Min 100
        account.setBalance(new BigDecimal("50.00")); // Violation
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        account = new AccountAggregate("acc-inactive");
        account.setStatus(AccountAggregate.AccountStatus.FROZEN); // Not Active
        account.setAccountNumber("888888");
        account.setType(AccountAggregate.AccountType.CHECKING);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        account = new AccountAggregate("acc-immutable");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setAccountNumber("111111");
        account.setAccountNumberImmutable(true); // Simulating existing immutable state
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed_for_rejection() {
        // Generic command execution, failure depends on state setup in Given
        // For immutability check, we supply a DIFFERENT account number in the command
        String targetAccountNumber = "111111"; 
        if (account.getAccountNumber() != null && account.getAccountNumber().equals("111111")) {
            targetAccountNumber = "222222"; // Attempt to change
        }

        UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(
            targetAccountNumber,
            AccountAggregate.AccountStatus.ACTIVE,
            null,
            null
        );
        
        // For the balance check, the aggregate logic checks its own state against the minimum of its type.
        // So passing nulls in cmd is fine, the invariant logic inside `handleUpdateStatus` will inspect `this.balance`.
        
        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Domain errors in this DDD pattern are typically RuntimeExceptions (IllegalStateException/IllegalArgumentException)
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
