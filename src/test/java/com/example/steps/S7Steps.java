package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    public void a_valid_account_aggregate() {
        this.accountNumber = "ACC-123";
        this.account = new AccountAggregate(accountNumber);
        this.account.setBalance(BigDecimal.ZERO);
        this.account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        this.account.setImmutableNumberSet(true);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // accountNumber already set in previous step
        assertNotNull(this.accountNumber);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        this.accountNumber = "ACC-BAL-ERR";
        this.account = new AccountAggregate(accountNumber);
        this.account.setBalance(new BigDecimal("100.50")); // Balance > 0
        this.account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        this.account.setImmutableNumberSet(true);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        this.accountNumber = "ACC-STAT-ERR";
        this.account = new AccountAggregate(accountNumber);
        this.account.setBalance(BigDecimal.ZERO);
        this.account.setStatus(AccountAggregate.AccountStatus.FROZEN); // Not ACTIVE
        this.account.setImmutableNumberSet(true);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        this.accountNumber = "ACC-IMM-ERR";
        this.account = new AccountAggregate(accountNumber);
        this.account.setBalance(BigDecimal.ZERO);
        this.account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        this.account.setImmutableNumberSet(true);
        // We will simulate a violation by attempting to close with a DIFFERENT number in the cmd
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            // If we are testing the immutability violation, we pass a mismatched number
            String cmdNumber = this.accountNumber;
            if (account != null && account.getAccountNumber().equals("ACC-IMM-ERR")) {
                cmdNumber = "DIFFERENT-NUMBER";
            }
            CloseAccountCmd cmd = new CloseAccountCmd(cmdNumber);
            this.resultEvents = account.execute(cmd);
            this.caughtException = null;
        } catch (Exception e) {
            this.caughtException = e;
            this.resultEvents = null;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect IllegalStateException, IllegalArgumentException, or RuntimeException indicating rejection
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException ||
                   caughtException instanceof UnknownCommandException);
        
        assertNull(resultEvents);
    }
}
