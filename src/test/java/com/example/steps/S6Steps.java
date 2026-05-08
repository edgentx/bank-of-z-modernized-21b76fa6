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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private String testAccountNumber;
    private AccountAggregate.AccountStatus testNewStatus;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("acct-123");
        account.setAccountNumber("ACC-001");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.valueOf(1000));
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        account = new AccountAggregate("acct-low-balance");
        account.setAccountNumber("ACC-LOW");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.valueOf(10)); // Assumed below minimum for this type
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        account = new AccountAggregate("acct-inactive");
        account.setAccountNumber("ACC-INACTIVE");
        account.setStatus(AccountAggregate.AccountStatus.FROZEN);
        account.setBalance(BigDecimal.valueOf(1000));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        account = new AccountAggregate("acct-immutable");
        account.setAccountNumber("ACC-ORIG");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.testAccountNumber = "ACC-001";
    }

    @And("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        this.testNewStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            // If scenarios didn't explicitly set these in 'And', set defaults for the command
            if (testAccountNumber == null) testAccountNumber = account.getAccountNumber();
            if (testNewStatus == null) testNewStatus = AccountAggregate.AccountStatus.FROZEN;

            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(account.id(), testAccountNumber, testNewStatus);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals(testNewStatus, event.newStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // In a real app, might be a specific DomainException, but RuntimeException or IllegalStateException is fine for this level
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
