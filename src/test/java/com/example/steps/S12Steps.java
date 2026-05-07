package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.ReverseTransactionCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.TransactionReversedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S12Steps {

    private TransactionAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate("tx-123");
        aggregate.setAmount(new BigDecimal("100.00"));
        aggregate.setAccountId("acct-1");
        aggregate.setPosted(true);
        aggregate.setReversed(false);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts() {
        aggregate = new TransactionAggregate("tx-invalid-amount");
        aggregate.setAmount(BigDecimal.ZERO);
        aggregate.setAccountId("acct-1");
        aggregate.setPosted(true);
        aggregate.setReversed(false);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        aggregate = new TransactionAggregate("tx-altered");
        aggregate.setAmount(new BigDecimal("50.00"));
        aggregate.setAccountId("acct-1");
        // Violation: Not posted
        aggregate.setPosted(false);
        aggregate.setReversed(false);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance() {
        aggregate = new TransactionAggregate("tx-invalid-balance");
        aggregate.setAmount(new BigDecimal("100.00"));
        // Violation: Account 999 triggers balance check failure in aggregate
        aggregate.setAccountId("999");
        aggregate.setPosted(true);
        aggregate.setReversed(false);
    }

    @And("a valid originalTransactionId is provided")
    public void a_valid_originalTransactionId_is_provided() {
        // Context setup provided in the 'Given' steps above
    }

    @When("the ReverseTransactionCmd command is executed")
    public void the_ReverseTransactionCmd_command_is_executed() {
        Command cmd = new ReverseTransactionCmd("tx-reverse-1", "original-tx-1");
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a transaction.reversed event is emitted")
    public void a_transaction_reversed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof TransactionReversedEvent);
        TransactionReversedEvent event = (TransactionReversedEvent) resultEvents.get(0);
        assertEquals("transaction.reversed", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
