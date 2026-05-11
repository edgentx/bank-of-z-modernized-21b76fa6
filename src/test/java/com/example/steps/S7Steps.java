package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate aggregate;
    private String accountNumber;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Simulate an opened, active account with zero balance (clean state for closing)
        aggregate = new AccountAggregate("acc-123");
        // Programmatically set state to valid 'Active' and Zero Balance for success scenario
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
        aggregate.setAccountNumber("123456789");
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalanceConstraint() {
        aggregate = new AccountAggregate("acc-invalid-bal");
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
        aggregate.setAccountNumber("987654321");
        // Balance is 100, min balance is 0. Cannot close because balance is not zero (or effectively > min)
        aggregate.setBalance(new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        aggregate = new AccountAggregate("acc-inactive");
        aggregate.setStatus(AccountAggregate.Status.DORMANT); // Not active
        aggregate.setAccountNumber("111222333");
        aggregate.setBalance(BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutability() {
        aggregate = new AccountAggregate("acc-mutability");
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
        aggregate.setBalance(BigDecimal.ZERO);
        // Using a null or empty account number to simulate violation of "valid/immutable" requirement check
        aggregate.setAccountNumber(null);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // This step is implicitly handled by the 'Given valid Account' setup, 
        // but we ensure the aggregate is in a clean state.
        if (aggregate == null) {
            aValidAccountAggregate();
        }
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            // The command contains the accountNumber intended for the operation
            Command cmd = new CloseAccountCmd(aggregate.getAccountNumber());
            resultEvents = aggregate.execute(cmd);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultEvents = null;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents, "Expected events to be emitted");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        Assertions.assertEquals("account.closed", resultEvents.get(0).type());
        Assertions.assertEquals("acc-123", resultEvents.get(0).aggregateId());
        Assertions.assertEquals(AccountAggregate.Status.CLOSED, aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        // Check it's a domain logic exception (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
            "Expected domain exception, got: " + thrownException.getClass().getSimpleName()
        );
    }
}
