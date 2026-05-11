package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.account.repository.AccountRepository;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class S5Steps {

    private AccountAggregate aggregate;
    private final AccountRepository repo = new InMemoryAccountRepository();
    private Exception capturedError;
    private List<DomainEvent> resultEvents;

    static class InMemoryAccountRepository implements AccountRepository {
        @Override public AccountAggregate load(String id) { return null; }
        @Override public void save(AccountAggregate aggregate) {}
        @Override public boolean existsByAccountNumber(String number) { return false; }
    }

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        aggregate = new AccountAggregate("acc-" + UUID.randomUUID());
    }

    @Given("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Context setup
    }

    @Given("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // Context setup
    }

    @Given("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // Context setup
    }

    @Given("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Context setup
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        Command cmd = new OpenAccountCmd("cust-123", "SAVINGS", new BigDecimal("100.00"), "10-20-30");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedError = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNull(capturedError, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        aggregate = new AccountAggregate("acc-violate-balance");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        aggregate = new AccountAggregate("acc-violate-status");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueness() {
        aggregate = new AccountAggregate("acc-violate-unique");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedError, "Expected exception was not thrown");
    }
}