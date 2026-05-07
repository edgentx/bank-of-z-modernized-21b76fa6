package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.DepositPostedEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class S10Steps {

    private TransactionAggregate aggregate;
    private String accountNumber;
    private BigDecimal amount;
    private String currency;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        aggregate = new TransactionAggregate("tx-1");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        this.accountNumber = "ACC-123";
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("100.00");
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_with_invalid_amount() {
        aggregate = new TransactionAggregate("tx-2");
        this.accountNumber = "ACC-123";
        this.amount = BigDecimal.ZERO;
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_transaction_aggregate_already_posted() {
        aggregate = new TransactionAggregate("tx-3");
        this.accountNumber = "ACC-123";
        this.amount = new BigDecimal("50.00");
        this.currency = "USD";
        
        // Manually set the aggregate to a posted state to simulate the violation
        aggregate.markAsPosted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_transaction_aggregate_invalid_balance() {
        aggregate = new TransactionAggregate("tx-4");
        this.accountNumber = "ACC-INVALID";
        this.amount = new BigDecimal("100.00");
        this.currency = "USD";
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        try {
            Command cmd = new PostDepositCmd(accountNumber, amount, currency);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof DepositPostedEvent);
        
        DepositPostedEvent event = (DepositPostedEvent) resultEvents.get(0);
        assertEquals("deposit.posted", event.type());
        assertEquals("tx-1", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect an IllegalArgumentException or IllegalStateException depending on specific invariant logic
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    @Given("Run Cucumber tests")
    public void run_cucumber_tests() {
        // Placeholder for JUnit Suite runner to find
    }
}
