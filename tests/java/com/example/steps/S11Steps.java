package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-11: PostWithdrawalCmd.
 * Located in tests/ as per DDD+Hex layout requirements.
 */
@SpringBootTest
public class S11Steps {

    private Transaction aggregate;
    private S11Event resultEvent;
    private Exception resultException;

    // --- Given ---

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        this.aggregate = new Transaction();
        this.aggregate.hydrate(new TransactionId("tx-123"), new AccountNumber("ACC-001"), BigDecimal.valueOf(1000.00), "USD");
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Context setup, implicitly used in 'When'
    }

    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Context setup
    }

    @Given("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Context setup
    }

    // --- Given (Negative Scenarios) ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsMustBeGreaterThanZero() {
        this.aggregate = new Transaction();
        this.aggregate.hydrate(new TransactionId("tx-bad-amount"), new AccountNumber("ACC-001"), BigDecimal.valueOf(1000.00), "USD");
        // Amount will be set to 0 in the When step trigger
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesImmutable() {
        this.aggregate = new Transaction();
        this.aggregate.hydrate(new TransactionId("tx-already-posted"), new AccountNumber("ACC-001"), BigDecimal.valueOf(1000.00), "USD");
        // Simulate that it's already posted
        this.aggregate.markPosted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesValidBalance() {
        this.aggregate = new Transaction();
        // Balance is 100, but we try to withdraw 200 in the When step
        this.aggregate.hydrate(new TransactionId("tx-insufficient-funds"), new AccountNumber("ACC-001"), BigDecimal.valueOf(100.00), "USD");
    }

    // --- When ---

    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        try {
            // Determine context based on state
            BigDecimal amount = BigDecimal.ZERO;
            
            if (aggregate.getId() != null && aggregate.getId().value().equals("tx-bad-amount")) {
                amount = BigDecimal.ZERO; // Invalid amount
            } else if (aggregate.getId() != null && aggregate.getId().value().equals("tx-already-posted")) {
                amount = BigDecimal.valueOf(50.00); // Valid amount, but aggregate state is bad
            } else if (aggregate.getId() != null && aggregate.getId().value().equals("tx-insufficient-funds")) {
                amount = BigDecimal.valueOf(200.00); // Overdraft
            } else {
                // Happy path
                amount = BigDecimal.valueOf(50.00);
            }

            PostWithdrawalCmd cmd = new PostWithdrawalCmd(new AccountNumber("ACC-001"), amount, "USD");
            this.resultEvent = this.aggregate.execute(cmd);
        } catch (DomainException e) {
            this.resultException = e;
        }
    }

    // --- Then ---

    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        assertNotNull(resultEvent, "Event should not be null");
        assertTrue(resultEvent instanceof WithdrawalPostedEvent, "Event should be WithdrawalPostedEvent");
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) resultEvent;
        assertEquals("ACC-001", event.accountNumber().value());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(resultException, "Exception should be thrown");
        assertTrue(resultException instanceof DomainException, "Exception should be DomainException");
    }
}
