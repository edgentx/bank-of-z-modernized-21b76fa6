package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public class S10Steps {

    // State for the current scenario
    private Transaction aggregate;
    private String providedAccountNumber;
    private BigDecimal providedAmount;
    private Currency providedCurrency;
    private DomainError capturedError;
    private DepositPostedEvent lastEvent;

    // Configuration stub (replace with Spring injection in integration tests)
    private final DomainConfig config = new DomainConfig(new BigDecimal("1000000000"));

    // Helper to create a clean slate
    private void resetState() {
        aggregate = new Transaction(UUID.randomUUID(), config);
        providedAccountNumber = null;
        providedAmount = null;
        providedCurrency = null;
        capturedError = null;
        lastEvent = null;
    }

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        resetState();
        // Aggregate is valid by default (new, empty balance)
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        this.providedAccountNumber = "ACC-12345";
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        this.providedAmount = new BigDecimal("100.00");
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        this.providedCurrency = Currency.getInstance("USD");
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        PostDepositCmd cmd = new PostDepositCmd(
            aggregate.getId(),
            providedAccountNumber,
            providedAmount,
            providedCurrency
        );
        try {
            lastEvent = aggregate.execute(cmd);
        } catch (DomainError e) {
            capturedError = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        Assertions.assertNotNull(lastEvent, "Expected an event to be emitted");
        Assertions.assertEquals(aggregate.getId(), lastEvent.transactionId());
        Assertions.assertEquals(providedAmount, lastEvent.amount());
        Assertions.assertEquals(providedAccountNumber, lastEvent.accountNumber());
    }

    // --- Negative Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountGreaterThanZero() {
        resetState();
        this.providedAmount = new BigDecimal("-50.00"); // Invalid amount
        // Assume other inputs are valid for simplicity unless specified otherwise
        this.providedAccountNumber = "ACC-123";
        this.providedCurrency = Currency.getInstance("USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesImmutability() {
        resetState();
        // To simulate violation, we manually mark the aggregate as posted internally
        // In a real repo, we would load a posted aggregate.
        // For this test, we can bypass by invoking a private setter or loading a state.
        // Here we assume the aggregate starts valid, we post one, then try to post another 
        // or we just set the state if possible. 
        // However, the aggregate handles state changes via events. 
        // To test "cannot be altered", we assume the aggregate was previously posted.
        
        // 1. Execute a valid command first to post it
        aggregate.execute(new PostDepositCmd(aggregate.getId(), "ACC-1", new BigDecimal("100"), Currency.getInstance("USD")));
        
        // 2. Setup inputs for the second attempt (which should fail)
        this.providedAccountNumber = "ACC-1";
        this.providedAmount = new BigDecimal("50");
        this.providedCurrency = Currency.getInstance("USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesBalanceLimit() {
        resetState();
        // Set current balance to near limit
        BigDecimal limit = config.getMaxTransactionAmount(); // 1B
        BigDecimal currentBalance = limit.subtract(BigDecimal.ONE);
        aggregate.setBalance(currentBalance);

        // Attempt to deposit 2.00, which exceeds the limit by 1.00
        this.providedAccountNumber = "ACC-999";
        this.providedAmount = new BigDecimal("2.00");
        this.providedCurrency = Currency.getInstance("USD");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedError, "Expected a DomainError to be thrown");
        Assertions.assertNull(lastEvent, "Expected no event to be emitted");
    }
}
