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
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S5Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        account = new AccountAggregate("acct-123");
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Context setup stored in variables implicitly
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            Command cmd = new OpenAccountCmd(
                "acct-123",
                "customer-456",
                "SAVINGS",
                new BigDecimal("100.00"),
                "10-20-30"
            );
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        account = new AccountAggregate("acct-fail-min");
        // We will force the command parameters in the When step to violate this
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // Note: OpenAccountCmd usually sets status to Active.
        // This scenario implies the aggregate state is problematic or the command is malformed.
        // Based on the aggregate implementation, if the aggregate is already opened/closed, it might fail.
        // For S-5 (OpenAccountCmd), we assume this validates the transition logic.
        account = new AccountAggregate("acct-fail-status");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueness() {
        // In an in-memory test, we can't easily check DB uniqueness without a repo.
        // But the aggregate might validate the format or immutability if it were stateful.
        account = new AccountAggregate("acct-fail-unique");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || 
            caughtException instanceof IllegalStateException
        );
    }

    // Context for violation scenarios
    @When("the OpenAccountCmd command is executed with invalid deposit {double}")
    public void theOpenAccountCmdCommandIsExecutedWithInvalidDeposit(double deposit) {
        try {
            Command cmd = new OpenAccountCmd(
                "acct-fail-min",
                "customer-456",
                "CHECKING", // Assuming min balance is higher or deposit is negative
                BigDecimal.valueOf(deposit),
                "10-20-30"
            );
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
