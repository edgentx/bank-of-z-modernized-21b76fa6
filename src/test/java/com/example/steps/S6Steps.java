package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate account;
    private String providedAccountNumber;
    private AccountAggregate.AccountStatus providedNewStatus;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Setup a valid account
        account = new AccountAggregate("ACC-123");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.valueOf(500.00));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        providedAccountNumber = "ACC-123";
    }

    @Given("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        providedNewStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(providedAccountNumber, providedNewStatus);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.status.updated", resultEvents.get(0).type());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        account = new AccountAggregate("ACC-LOW-BAL");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.valueOf(50.00)); // Below 100.00
        providedAccountNumber = "ACC-LOW-BAL";
        providedNewStatus = AccountAggregate.AccountStatus.FROZEN;
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status_requirement() {
        account = new AccountAggregate("ACC-NOT-ACTIVE");
        account.setStatus(AccountAggregate.AccountStatus.FROZEN); // Not Active
        account.setBalance(BigDecimal.valueOf(500.00));
        providedAccountNumber = "ACC-NOT-ACTIVE";
        providedNewStatus = AccountAggregate.AccountStatus.CLOSED;
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_number_immutability() {
        account = new AccountAggregate("ACC-ORIG");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.valueOf(500.00));
        // Provide a mismatched number
        providedAccountNumber = "ACC-NEW-IMMUTABLE";
        providedNewStatus = AccountAggregate.AccountStatus.CLOSED;
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // Domain errors in this pattern are usually IllegalStateExceptions or RuntimeExceptions
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
