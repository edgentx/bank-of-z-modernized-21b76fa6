package com.example.steps;

import com.example.domain.shared.Command;
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
    private Exception caughtException;
    private List<DomainEvent> resultEvents;

    // Scenario: Successfully execute PostDepositCmd
    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate("txn-123");
        // Starting state: not posted, 0 balance effect
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Context used in When step
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Context used in When step
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Context used in When step
    }

    // Scenario: PostDepositCmd rejected — Transaction amounts must be greater than zero.
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_must_be_positive() {
        aggregate = new TransactionAggregate("txn-invalid-amount");
    }

    // Scenario: PostDepositCmd rejected — Transactions cannot be altered or deleted once posted
    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_already_posted() {
        aggregate = new TransactionAggregate("txn-already-posted");
        // Simulate posted state by mutating directly (tests handling internal state representation)
        aggregate.markPostedInternal(); 
    }

    // Scenario: PostDepositCmd rejected — A transaction must result in a valid account balance
    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_balance() {
        aggregate = new TransactionAggregate("txn-invalid-balance");
        aggregate.setSimulatedCurrentBalance(BigDecimal.valueOf(100));
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        caughtException = null;
        try {
            // Determine context based on state setup
            String account = "acc-456";
            BigDecimal amount = BigDecimal.valueOf(50);
            String currency = "USD";

            // Adjust inputs based on specific test contexts implied by the violation state
            if (aggregate.getId().equals("txn-invalid-amount")) {
                amount = BigDecimal.ZERO;
            } else if (aggregate.getId().equals("txn-invalid-balance")) {
                // If balance is 100, we assume -200 causes an overflow or invalid state based on requirements
                // Or just a negative final balance. Let's assume -200 makes balance -100 which is invalid.
                amount = BigDecimal.valueOf(-200); 
            }

            Command cmd = new PostDepositCmd(aggregate.getId(), account, amount, currency);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNull(caughtException, "Expected no exception, but got: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof DepositPostedEvent);
        DepositPostedEvent event = (DepositPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("deposit.posted", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException, "Expected a domain error exception");
        // In a real app we might catch a custom DomainException, here we check for RuntimeException/IllegalStateException
        Assertions.assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
