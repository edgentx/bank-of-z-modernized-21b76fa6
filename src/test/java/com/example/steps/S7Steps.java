package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate account;
    private String accountNumber;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        accountNumber = "ACC-123";
        account = new AccountAggregate(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // accountNumber is already set in the previous step
        assertNotNull(accountNumber);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesAccountBalanceCannotDropBelowTheMinimumRequiredBalance() {
        accountNumber = "ACC-BAL-ERR";
        account = new AccountAggregate(accountNumber);
        account.setBalance(new BigDecimal("100.00"));
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesAnAccountMustBeInAnActiveStatus() {
        accountNumber = "ACC-STATUS-ERR";
        account = new AccountAggregate(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.FROZEN);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesAccountNumbersMustBeUniquelyGenerated() {
        // This implies the command targets an account ID different from the aggregate's root ID
        accountNumber = "ACC-IMMUTABLE";
        account = new AccountAggregate(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            // For the immutability violation scenario, we use a different account number in the command
            String cmdAccountNumber = account.getStatus() == AccountAggregate.AccountStatus.FROZEN ? accountNumber : accountNumber;
            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0 && account.getBalance().compareTo(new BigDecimal("100.00")) == 0) {
                cmdAccountNumber = accountNumber;
            }
            
            // If status is frozen, we use the valid number. If balance is 100, we use valid number.
            // Only if we want to test immutability do we swap it. The Gherkin is vague on how to trigger this.
            // Based on "Account numbers must be uniquely generated and immutable", and the command taking an ID.
            // We'll assume the violation scenario implies the aggregate ID is somehow invalid or the command targets a different ID.
            // However, aggregates are instantiated by ID. The check `cmd.accountNumber().equals(this.accountNumber)` handles this.
            // We will construct the command with the aggregate's ID to pass other tests, and a different ID to fail this one.
            
            String effectiveCmdNumber = accountNumber;
            if (account.id().equals("ACC-IMMUTABLE")) {
                effectiveCmdNumber = "DIFFERENT-ID";
            }

            CloseAccountCmd cmd = new CloseAccountCmd(effectiveCmdNumber);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertTrue(account.isClosed());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Check for domain exception types (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
