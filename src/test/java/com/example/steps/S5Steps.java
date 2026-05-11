package com.example.steps;

import com.example.domain.account.model.*;
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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test Data constants
    private static final String VALID_CUSTOMER_ID = "CUST-123";
    private static final String VALID_SORT_CODE = "10-20-30";
    private static final AccountAggregate.AccountType VALID_TYPE = AccountAggregate.AccountType.SAVINGS;
    private static final BigDecimal VALID_DEPOSIT = new BigDecimal("150.00"); // Meets min 100 for Savings

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        account = new AccountAggregate("ACC-NEW-1");
        capturedException = null;
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Data is stored in the context, implicitly used in command construction
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // Context
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // Context
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Context
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        Command cmd = new OpenAccountCmd(
            account.id(),
            VALID_CUSTOMER_ID,
            VALID_TYPE,
            VALID_DEPOSIT,
            VALID_SORT_CODE
        );
        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        Assertions.assertEquals("account.opened", event.type());
        Assertions.assertEquals(VALID_CUSTOMER_ID, event.customerId());
        Assertions.assertEquals(VALID_TYPE, event.accountType());
        Assertions.assertEquals(0, VALID_DEPOSIT.compareTo(event.initialBalance()));
        Assertions.assertEquals(VALID_SORT_CODE, event.sortCode());
    }

    // --- Scenarios for Rejections ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinBalance() {
        // We simulate this violation by setting up the command with a low deposit in the 'When' step
        account = new AccountAggregate("ACC-LOW-1");
    }

    // Override When for this specific scenario to inject invalid data
    @When("the OpenAccountCmd command is executed with low deposit")
    public void theOpenAccountCmdCommandIsExecutedWithLowDeposit() {
        // Savings min is 100.00, we give 50.00
        Command cmd = new OpenAccountCmd(
            account.id(),
            VALID_CUSTOMER_ID,
            AccountAggregate.AccountType.SAVINGS,
            new BigDecimal("50.00"),
            VALID_SORT_CODE
        );
        try {
            resultEvents = account.execute(cmd);
        } catch (IllegalArgumentException e) {
            capturedException = e;
        }
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        account = new AccountAggregate("ACC-INACTIVE");
        account.markClosed(); // Force status to Closed
    }

    @When("the OpenAccountCmd command is executed on inactive account")
    public void theOpenAccountCmdCommandIsExecutedOnInactiveAccount() {
        Command cmd = new OpenAccountCmd(
            account.id(),
            VALID_CUSTOMER_ID,
            VALID_TYPE,
            VALID_DEPOSIT,
            VALID_SORT_CODE
        );
        try {
            resultEvents = account.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutability() {
        account = new AccountAggregate("ACC-EXISTING");
        account.markImmutable(); // Simulate existing immutable state
    }

    @When("the OpenAccountCmd command is executed on immutable account")
    public void theOpenAccountCmdCommandIsExecutedOnImmutableAccount() {
        Command cmd = new OpenAccountCmd(
            account.id(),
            VALID_CUSTOMER_ID,
            VALID_TYPE,
            VALID_DEPOSIT,
            VALID_SORT_CODE
        );
        try {
            resultEvents = account.execute(cmd);
        } catch (IllegalStateException e) {
            capturedException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Verify it is a runtime exception indicating domain logic failure
        Assertions.assertTrue(
            capturedException instanceof IllegalArgumentException || 
            capturedException instanceof IllegalStateException
        );
    }
}
