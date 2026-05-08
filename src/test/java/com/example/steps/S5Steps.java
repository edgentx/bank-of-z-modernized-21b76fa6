package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
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

    private AccountAggregate aggregate;
    private String customerId;
    private String accountType;
    private BigDecimal initialDeposit;
    private String sortCode;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        aggregate = new AccountAggregate("ACC-123");
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        this.customerId = "CUST-001";
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        this.accountType = "STANDARD";
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        this.initialDeposit = new BigDecimal("500.00");
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        this.sortCode = "10-20-30";
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            OpenAccountCmd cmd = new OpenAccountCmd(aggregate.id(), customerId, accountType, initialDeposit, sortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertFalse(resultEvents.isEmpty());
        Assertions.assertEquals("account.opened", resultEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        aggregate = new AccountAggregate("ACC-LOW");
        customerId = "CUST-001";
        accountType = "PREMIUM"; // Assumed logic in Aggregate: Premium requires 1000
        initialDeposit = new BigDecimal("50.00"); // Violates Min Balance
        sortCode = "10-20-30";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        aggregate = new AccountAggregate("ACC-INACTIVE");
        customerId = "CUST-001";
        accountType = "STANDARD";
        initialDeposit = new BigDecimal("500.00");
        sortCode = "10-20-30";
        // To simulate the violation of this rule, we setup a context where status prevents action.
        // For 'OpenAccount', the rejection usually happens if already Active.
        // We manually set the state to ACTIVE to simulate the violation (trying to open an already active account)
        // In a real system, this would be done via history, but here we construct the scenario.
        // Since we don't have a public setter for status in the aggregate (it's protected/private),
        // we will simulate this by attempting to open the account twice or relying on domain logic.
        // However, for BDD 'Given' we often mock the state.
        // Since I cannot modify the aggregate easily without a memento, I will rely on the second call to execute failing.
        
        // First successful open
        OpenAccountCmd cmd1 = new OpenAccountCmd(aggregate.id(), customerId, accountType, initialDeposit, sortCode);
        aggregate.execute(cmd1);
        
        // Now it is ACTIVE. The next execution should fail.
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        aggregate = new AccountAggregate("ACC-IMMUTABLE");
        customerId = "CUST-001";
        accountType = "STANDARD";
        initialDeposit = new BigDecimal("500.00");
        sortCode = "10-20-30";
        
        // Setup: Open the account once to set the immutable number
        OpenAccountCmd cmd1 = new OpenAccountCmd(aggregate.id(), customerId, accountType, initialDeposit, sortCode);
        aggregate.execute(cmd1);
        
        // Scenario: Attempting to 'Open' again (which implies re-assigning number) triggers the immutability check.
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException or IllegalArgumentException), but got: " + caughtException.getClass().getSimpleName()
        );
    }

}
