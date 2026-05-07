package com.example.steps;

import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.DepositPostedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class S10Steps {

    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate("tx-1", "acc-123", BigDecimal.ZERO, "USD");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled by aggregate initialization in context
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled by context setup
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled by context setup
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        aggregate = new TransactionAggregate("tx-2", "acc-123", BigDecimal.ZERO, "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_locked_state() {
        aggregate = new TransactionAggregate("tx-3", "acc-123", BigDecimal.ZERO, "USD");
        // Manually set state to posted to simulate the invariant violation
        aggregate.markPosted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_balance() {
        aggregate = new TransactionAggregate("tx-4", "acc-123", BigDecimal.ZERO, "USD") {
            @Override
            protected void validateBalance(String accountNumber, BigDecimal amount) {
                throw new IllegalStateException("Invariant violated: Invalid resulting balance");
            }
        };
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Construct a valid command for the happy path or specific violations handled by aggregate state
            Command cmd = new PostDepositCmd("acc-123", new BigDecimal("100.00"), "USD");
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
        assertEquals("deposit.posted", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalArgumentException || 
                   caughtException instanceof IllegalStateException);
    }
}
