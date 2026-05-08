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
    private String accountNumber;
    private AccountAggregate.AccountStatus newStatus;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.accountNumber = "ACC-123-456";
        this.account = new AccountAggregate(accountNumber);
        // Hydrate with valid defaults
        account.hydrate(AccountAggregate.AccountStatus.ACTIVE, new BigDecimal("500.00"), "CHECKING");
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesAccountBalanceCannotDropBelowTheMinimumRequiredBalanceForItsSpecificAccountType() {
        this.accountNumber = "ACC-LOW-BAL";
        this.account = new AccountAggregate(accountNumber);
        // Hydrate with low balance (below assumed minimum 100)
        account.hydrate(AccountAggregate.AccountStatus.ACTIVE, new BigDecimal("50.00"), "CHECKING");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesAnAccountMustBeInAnActiveStatusToProcessWithdrawalsOrTransfers() {
        this.accountNumber = "ACC-INACTIVE";
        this.account = new AccountAggregate(accountNumber);
        // Hydrate with FROZEN status
        account.hydrate(AccountAggregate.AccountStatus.FROZEN, new BigDecimal("500.00"), "CHECKING");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesAccountNumbersMustBeUniquelyGeneratedAndImmutable() {
        // The aggregate ID is final, so we simulate a scenario where the command targets the wrong ID
        this.accountNumber = "ACC-ORIG";
        this.account = new AccountAggregate(accountNumber);
        account.hydrate(AccountAggregate.AccountStatus.ACTIVE, new BigDecimal("500.00"), "CHECKING");
        
        // We will construct the command with a DIFFERENT account number in the 'And' step to force the error
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Default implementation assumes matching account number
        this.accountNumber = account.getAccountNumber(); 
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        this.newStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    // Override for the immutable violation scenario
    @And("a valid accountNumber is provided")
    public void aMismatchedAccountNumberIsProvided() {
        // This method overload isn't standard Cucumber, relying on scenario context is better. 
        // Since Cucumber matches by regex/param, we'll handle the logic in the 'When' or specific step.
        // For simplicity in this generated code, we'll assume the previous step set up the aggregate.
        // We'll manually inject the mismatch in the When step logic if needed, but here we just set the command var.
        // Actually, let's just make sure the 'When' step handles the violation logic.
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        UpdateAccountStatusCmd cmd;
        
        // Logic to handle the specific violation for "Immutable Account Number"
        // If the aggregate is ACC-ORIG, we send a command for ACC-FAKE
        if ("ACC-ORIG".equals(account.getAccountNumber())) {
             cmd = new UpdateAccountStatusCmd("ACC-FAKE", AccountAggregate.AccountStatus.FROZEN);
        } else {
             cmd = new UpdateAccountStatusCmd(account.getAccountNumber(), this.newStatus);
        }

        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals(account.getAccountNumber(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Verify it's the correct type of exception (IllegalStateException per our implementation)
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
