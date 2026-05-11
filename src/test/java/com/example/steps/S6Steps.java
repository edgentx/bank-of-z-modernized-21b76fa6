package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate aggregate;
    private String accountNumber;
    private AccountAggregate.AccountStatus newStatus;
    private Exception thrownException;
    private List<DomainEvent> resultingEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        this.accountNumber = "ACC-TEST-001";
        this.aggregate = new AccountAggregate(accountNumber);
        // Ensure valid state
        aggregate.hydrate(AccountAggregate.AccountStatus.ACTIVE, BigDecimal.valueOf(1000), AccountAggregate.AccountType.CHECKING);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // accountNumber initialized in previous step or defaulted
        if (this.accountNumber == null) this.accountNumber = "ACC-TEST-002";
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        this.newStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        this.accountNumber = "ACC-LOW-BAL";
        this.aggregate = new AccountAggregate(accountNumber);
        // Savings min balance is 100.00, set to 50.00
        aggregate.hydrate(AccountAggregate.AccountStatus.ACTIVE, BigDecimal.valueOf(50.00), AccountAggregate.AccountType.SAVINGS);
        this.newStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        this.accountNumber = "ACC-NOT-ACTIVE";
        this.aggregate = new AccountAggregate(accountNumber);
        aggregate.hydrate(AccountAggregate.AccountStatus.FROZEN, BigDecimal.valueOf(500), AccountAggregate.AccountType.CHECKING);
        this.newStatus = AccountAggregate.AccountStatus.CLOSED;
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_uniqueness() {
        this.accountNumber = "ACC-IMMUTABLE";
        this.aggregate = new AccountAggregate(accountNumber);
        aggregate.hydrate(AccountAggregate.AccountStatus.ACTIVE, BigDecimal.valueOf(100), AccountAggregate.AccountType.CHECKING);
        this.newStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        Command cmd;
        
        // Simulating the uniqueness violation by targeting a different ID than the aggregate
        if (accountNumber.equals("ACC-IMMUTABLE")) {
             cmd = new UpdateAccountStatusCmd("DIFFERENT-ID", newStatus);
        } else {
             cmd = new UpdateAccountStatusCmd(accountNumber, newStatus);
        }

        try {
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("account.status.updated", resultingEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // We check for common domain exception types or message content
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
