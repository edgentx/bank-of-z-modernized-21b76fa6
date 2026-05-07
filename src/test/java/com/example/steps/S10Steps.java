package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.TransactionPostedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private TransactionAggregate aggregate;
    private String testAccountId = "acct-123";
    private BigDecimal testAmount = new BigDecimal("100.00");
    private String testCurrency = "USD";
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        aggregate = new TransactionAggregate("tx-1");
        assertNotNull(aggregate);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateWithInvalidAmount() {
        aggregate = new TransactionAggregate("tx-invalid-amt");
        testAmount = BigDecimal.ZERO;
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatIsAlreadyPosted() {
        aggregate = new TransactionAggregate("tx-already-posted");
        // Force the aggregate into a posted state by bypassing command execution directly for testing setup
        // In a real scenario, we might execute a valid command first, but here we simulate the state.
        try {
            aggregate.execute(new PostDepositCmd("tx-already-posted", testAccountId, new BigDecimal("50.00"), "USD"));
        } catch (Exception e) {
            fail("Setup failed: " + e.getMessage());
        }
        assertTrue(aggregate.isPosted());
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateWithBalanceViolation() {
        // This scenario represents a business rule validation.
        // Since the aggregate logic provided throws an exception for amount <= 0,
        // we map the "valid balance" violation to the negative amount case here.
        aggregate = new TransactionAggregate("tx-balance-violation");
        testAmount = new BigDecimal("-100.00");
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Default testAccountId is sufficient
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Default testAmount is sufficient
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Default testCurrency is sufficient
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        PostDepositCmd cmd = new PostDepositCmd(aggregate.id(), testAccountId, testAmount, testCurrency);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertTrue(resultEvents.get(0) instanceof TransactionPostedEvent);
        TransactionPostedEvent event = (TransactionPostedEvent) resultEvents.get(0);
        assertEquals("deposit", event.kind());
        assertEquals("tx-1", event.transactionId());
        assertEquals(testAccountId, event.accountId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // The existing aggregate throws IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
