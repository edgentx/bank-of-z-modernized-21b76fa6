package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S11Steps {

    private Transaction transaction;
    private PostWithdrawalCmd command;
    private WithdrawalPostedEvent resultEvent;
    private Exception domainError;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        // Initialize a valid, unposted transaction
        transaction = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        if (command == null) {
            command = new PostWithdrawalCmd();
        }
        command.setAccountNumber("ACC-1001");
    }

    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        if (command == null) {
            command = new PostWithdrawalCmd();
        }
        command.setAmount(new BigDecimal("50.00"));
    }

    @Given("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        if (command == null) {
            command = new PostWithdrawalCmd();
        }
        command.setCurrency("USD");
    }

    // --- Violations & Error Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmount() {
        transaction = new Transaction(UUID.randomUUID());
        command = new PostWithdrawalCmd();
        command.setAccountNumber("ACC-1001");
        command.setAmount(BigDecimal.ZERO); // Violation
        command.setCurrency("USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesImmutability() {
        transaction = new Transaction(UUID.randomUUID());
        transaction.markPosted(); // The aggregate is now immutable
        
        command = new PostWithdrawalCmd();
        command.setAccountNumber("ACC-1001");
        command.setAmount(new BigDecimal("10.00"));
        command.setCurrency("USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesBalanceValidation() {
        // In a real scenario, this might involve checking the account repo.
        // Here we simulate the aggregate being configured to reject a specific overdraft.
        transaction = new Transaction(UUID.randomUUID());
        transaction.setAllowOverdraft(false); // Constraint: Balance cannot go below 0
        transaction.setCurrentBalance(new BigDecimal("0.00"));

        command = new PostWithdrawalCmd();
        command.setAccountNumber("ACC-1001");
        command.setAmount(new BigDecimal("100.00")); // Would overdraft
        command.setCurrency("USD");
    }

    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        try {
            resultEvent = transaction.execute(command);
        } catch (IllegalStateException | IllegalArgumentException e) {
            domainError = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        Assertions.assertNotNull(resultEvent, "Event should not be null");
        Assertions.assertNotNull(resultEvent.getEventId());
        Assertions.assertEquals(command.getAccountNumber(), resultEvent.getAccountNumber());
        Assertions.assertEquals(0, command.getAmount().compareTo(resultEvent.getAmount()));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(domainError, "Expected a domain error exception");
        Assertions.assertNull(resultEvent, "No event should be emitted when command is rejected");
    }
}
