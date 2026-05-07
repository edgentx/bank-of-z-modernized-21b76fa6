package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Step Definitions for Story S-10.
 * Uses In-Memory aggregates for isolated testing.
 */
public class S10Steps {

    // Test context variables
    private Transaction transaction;
    private PostDepositCmd cmd;
    private Exception capturedException;

    // Constants for valid data
    private static final UUID VALID_TX_ID = UUID.randomUUID();
    private static final String VALID_ACCOUNT = "ACC-123";
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("100.00");
    private static final Currency VALID_CURRENCY = Currency.getInstance("USD");

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        DomainConfig config = DomainConfig.defaults();
        this.transaction = new Transaction(VALID_TX_ID, VALID_ACCOUNT, config);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Account number is usually tied to the aggregate, but command must match.
        // Handled in command construction.
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Amount is valid (> 0), handled in command construction.
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Currency is valid, handled in command construction.
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        // Construct the command with valid defaults set in Givens
        if (cmd == null) {
            this.cmd = new PostDepositCmd(VALID_TX_ID, VALID_ACCOUNT, VALID_AMOUNT, VALID_CURRENCY);
        }
        
        try {
            transaction.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertEquals(1, transaction.getEvents().size());
        assertTrue(transaction.getEvents().get(0) instanceof DepositPostedEvent);
        
        DepositPostedEvent event = (DepositPostedEvent) transaction.getEvents().get(0);
        assertEquals(VALID_TX_ID, event.transactionId());
        assertEquals(VALID_ACCOUNT, event.accountNumber());
        assertEquals(VALID_AMOUNT, event.amount());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected a domain error but none was thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    // --- Failure Scenario Givens ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsMustBeGreaterThanZero() {
        aValidTransactionAggregate(); // Standard setup
        // Override the command to be created with invalid amount in the 'When' step
        // We set the command here so the When step uses it instead of creating a default.
        this.cmd = new PostDepositCmd(VALID_TX_ID, VALID_ACCOUNT, BigDecimal.ZERO, VALID_CURRENCY);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesCannotBeAlteredOncePosted() {
        aValidTransactionAggregate();
        // Simulate the transaction is already posted
        transaction.setPosted(true);
        
        // Prepare a valid command (rejection comes from state, not command data)
        this.cmd = new PostDepositCmd(VALID_TX_ID, VALID_ACCOUNT, VALID_AMOUNT, VALID_CURRENCY);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidAccountBalance() {
        DomainConfig config = DomainConfig.defaults(); // Max 100M
        this.transaction = new Transaction(VALID_TX_ID, VALID_ACCOUNT, config);
        
        // Set balance near max limit to force overflow on deposit
        transaction.setBalance(new BigDecimal("99999999.00"));
        
        // Prepare a command that will push it over the edge
        this.cmd = new PostDepositCmd(VALID_TX_ID, VALID_ACCOUNT, new BigDecimal("100.00"), VALID_CURRENCY);
    }
}
