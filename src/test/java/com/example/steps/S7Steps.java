package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        account = new AccountAggregate("acct-1");
        account.setAccountNumber("123456");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_non_zero_balance() {
        account = new AccountAggregate("acct-2");
        account.setAccountNumber("123456");
        account.setBalance(new BigDecimal("100.00")); // Non-zero balance
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_with_invalid_status() {
        account = new AccountAggregate("acct-3");
        account.setAccountNumber("123456");
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.SUSPENDED); // Not ACTIVE
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_with_mismatched_account_number() {
        account = new AccountAggregate("acct-4");
        account.setAccountNumber("111111"); // Stored number
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Normally implicit in the command construction, ensuring we use the correct number for the account
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            String cmdAccountNumber = (account.getAccountNumber() != null) ? account.getAccountNumber() : "000000";
            CloseAccountCmd cmd = new CloseAccountCmd(account.id(), cmdAccountNumber);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verifying it's a logic/domain error (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
