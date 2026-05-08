package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private String accountNumber;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.accountNumber = "ACC-123";
        this.account = new AccountAggregate(this.accountNumber);
        this.account.setBalance(BigDecimal.ZERO);
        this.account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled in the previous step setup, ensuring the command matches the aggregate ID.
        assertNotNull(this.accountNumber);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesAccountBalanceCannotDropBelowTheMinimumRequiredBalanceForItsSpecificAccountType() {
        this.accountNumber = "ACC-456";
        this.account = new AccountAggregate(this.accountNumber);
        // Simulate a non-zero balance which violates the "zero balance required to close" rule (derived invariant)
        this.account.setBalance(new BigDecimal("100.00"));
        this.account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesAnAccountMustBeInAnActiveStatusToProcessWithdrawalsOrTransfers() {
        this.accountNumber = "ACC-789";
        this.account = new AccountAggregate(this.accountNumber);
        this.account.setBalance(BigDecimal.ZERO);
        this.account.setStatus(AccountAggregate.AccountStatus.DORMANT); // Not active
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesAccountNumbersMustBeUniquelyGeneratedAndImmutable() {
        // To simulate a mismatch/violation in the context of the aggregate, 
        // we setup the aggregate with one ID but issue a command with another, 
        // simulating a "stale" or "conflicting" command intent regarding identity.
        this.accountNumber = "ACC-999";
        this.account = new AccountAggregate(this.accountNumber);
        this.account.setBalance(BigDecimal.ZERO);
        this.account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            // For the "Account numbers must be uniquely generated and immutable" scenario,
            // we intentionally pass a different accountNumber in the command than the aggregate ID
            // to trigger the validation error inside the aggregate.
            String cmdAccountNumber = this.accountNumber;
            if (this.account.id().equals("ACC-999")) {
                cmdAccountNumber = "INVALID-ID";
            }
            
            CloseAccountCmd cmd = new CloseAccountCmd(cmdAccountNumber);
            this.resultEvents = account.execute(cmd);
        } catch (Exception e) {
            this.caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals(accountNumber, resultEvents.get(0).aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain logic exceptions are usually IllegalStateException or IllegalArgumentException
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
