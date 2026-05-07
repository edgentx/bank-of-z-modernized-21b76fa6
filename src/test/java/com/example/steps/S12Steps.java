package com.example.steps;

import com.example.domain.ReverseTransactionCmd;
import com.example.domain.TransactionReversedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.TransactionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S12Steps {
    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;
    private ReverseTransactionCmd cmd;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        // Create a valid aggregate (unposted, ready for reversal)
        aggregate = new TransactionAggregate("tx-123", 100.00, "acc-456", false);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesAmount() {
        aggregate = new TransactionAggregate("tx-123", 100.00, "acc-456", false);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesPosted() {
        // Create a posted transaction to simulate the violation
        aggregate = new TransactionAggregate("tx-123", 100.00, "acc-456", false);
        aggregate.markPosted(); // Simulate it has been posted
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesBalance() {
        // Create a valid aggregate structurally, but the command amount will trigger the balance failure
        aggregate = new TransactionAggregate("tx-123", 100.00, "acc-456", false);
    }

    @And("a valid originalTransactionId is provided")
    public void aValidOriginalTransactionIdIsProvided() {
        // We construct the command with valid data here, it is executed in the When step
        cmd = new ReverseTransactionCmd("rev-123", "orig-tx-999", 100.00, "acc-456", false);
    }

    @And("a valid originalTransactionId is provided with a zero amount")
    public void aValidOriginalTransactionIdIsProvidedWithZeroAmount() {
        cmd = new ReverseTransactionCmd("rev-123", "orig-tx-999", 0.0, "acc-456", false);
    }

    @And("a valid originalTransactionId is provided for a posted transaction")
    public void aValidOriginalTransactionIdIsProvidedForPostedTx() {
        cmd = new ReverseTransactionCmd("rev-123", "orig-tx-999", 100.00, "acc-456", false);
    }

    @And("a valid originalTransactionId is provided that causes invalid balance")
    public void aValidOriginalTransactionIdThatCausesInvalidBalance() {
        // In our model, amount > 10000 triggers invalid balance
        cmd = new ReverseTransactionCmd("rev-123", "orig-tx-999", 10001.00, "acc-456", false);
    }

    @When("the ReverseTransactionCmd command is executed")
    public void theReverseTransactionCmdCommandIsExecuted() {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void aTransactionReversedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
        assertEquals("transaction.reversed", resultEvents.get(0).type());
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    // Additional wiring for Cucumber to discover steps for the specific constraints
    @When("the ReverseTransactionCmd command is executed")
    public void theReverseTransactionCmdCommandIsExecutedForConstraints() {
        this.theReverseTransactionCmdCommandIsExecuted();
    }

    // Parameter matching for Gherkin constraints
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void setupAmountViolation() {
        aValidOriginalTransactionIdIsProvidedWithZeroAmount();
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void setupPostedViolation() {
        aTransactionAggregateThatViolatesPosted();
        aValidOriginalTransactionIdIsProvidedForPostedTx();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void setupBalanceViolation() {
        aTransactionAggregateThatViolatesBalance();
        aValidOriginalTransactionIdThatCausesInvalidBalance();
    }
}
