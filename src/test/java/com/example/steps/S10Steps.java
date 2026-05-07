package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.DepositPostedEvent;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S10Steps {

    private TransactionAggregate aggregate;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        aggregate = new TransactionAggregate("TXN-123");
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateWithInvalidAmount() {
        aggregate = new TransactionAggregate("TXN-INVALID-AMT");
        amount = BigDecimal.ZERO; // Violation
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatIsAlreadyPosted() {
        aggregate = new TransactionAggregate("TXN-ALREADY-POSTED");
        aggregate.markPosted(); // Violation setup
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateWithInvalidAccount() {
        aggregate = new TransactionAggregate("TXN-INVALID-ACCT");
        accountId = null; // Violation
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        if (accountId == null) accountId = "ACC-456";
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        if (amount == null) amount = new BigDecimal("100.00");
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        if (currency == null) currency = "USD";
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        try {
            // If not explicitly set invalid in Given, use valid defaults
            if (accountId == null) accountId = "ACC-456";
            if (amount == null) amount = new BigDecimal("50.00");
            if (currency == null) currency = "USD";

            PostDepositCmd cmd = new PostDepositCmd(aggregate.id(), accountId, amount, currency);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof DepositPostedEvent);
        DepositPostedEvent event = (DepositPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("deposit.posted", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(capturedException);
        // Check if it's an IllegalStateException or IllegalArgumentException
        Assertions.assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}
