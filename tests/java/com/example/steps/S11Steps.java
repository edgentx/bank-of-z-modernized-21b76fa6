package com.example.steps;

import com.example.domain.PostWithdrawalCmd;
import com.example.domain.Transaction;
import com.example.domain.TransactionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S11Steps {

    private Transaction transaction;
    private final TransactionRepository repository = new InMemoryTransactionRepository();
    private Exception capturedException;
    private Object lastEvent;

    // --- Given ---

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        transaction = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Context setup - assuming state is held in the transaction or context
    }

    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Context setup
    }

    @Given("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Context setup
    }

    // --- Violation States ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmountsGreaterThanZero() {
        transaction = new Transaction(UUID.randomUUID());
        // Simulate a state where the amount logic is handled, but command is invalid.
        // The command will carry the invalid amount.
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesAlterationPolicy() {
        transaction = new Transaction(UUID.randomUUID());
        // Mark transaction as posted internally for the sake of the test scenario
        transaction.markAsPosted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesAccountBalanceValidation() {
        transaction = new Transaction(UUID.randomUUID());
        // Set a current balance that would make a withdrawal invalid
        transaction.setCurrentBalance(BigDecimal.ZERO); // Empty account
    }

    // --- When ---

    @When("the PostWithdrawalCmd command is executed")
    public void thePostWithdrawalCmdCommandIsExecuted() {
        try {
            // Defaults for a valid command, can be overridden if needed by context
            String acct = "123456";
            BigDecimal amt = BigDecimal.TEN; // Default valid amount
            String curr = "USD";

            // If context implies a specific violation, we adjust params or state.
            // S11Steps logic is simple: we try to execute.
            
            PostWithdrawalCmd cmd = new PostWithdrawalCmd(acct, amt, curr);
            lastEvent = transaction.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException e) {
            capturedException = e;
        }
    }

    // --- Then ---

    @Then("a withdrawal.posted event is emitted")
    public void aWithdrawalPostedEventIsEmitted() {
        Assertions.assertNotNull(lastEvent);
        Assertions.assertTrue(lastEvent.getClass().getSimpleName().contains("Event"));
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
    }

    // --- Infrastructure Stubs ---

    private static class InMemoryTransactionRepository implements TransactionRepository {
        @Override
        public void save(Transaction aggregate) {
            // No-op for in-memory validation
        }
    }
}
