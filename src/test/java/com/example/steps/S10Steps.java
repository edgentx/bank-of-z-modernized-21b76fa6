package com.example.steps;

import com.example.domain.transaction.model.PostDepositCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.DepositPostedEvent;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private TransactionAggregate aggregate;
    private PostDepositCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_transaction_aggregate() {
        aggregate = new TransactionAggregate("TXN-123", "ACC-456", BigDecimal.ZERO, "USD");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Handled in constructor setup, or explicitly here if needed for specific command context
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled in command setup
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled in command setup
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_transaction_aggregate_that_violates_amount_gt_zero() {
        aggregate = new TransactionAggregate("TXN-INVALID", "ACC-456", BigDecimal.ZERO, "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_transaction_aggregate_that_violates_immutability() {
        aggregate = new TransactionAggregate("TXN-POSTED", "ACC-456", BigDecimal.ZERO, "USD");
        // Force state to posted for the sake of the violation scenario
        // In a real repo we would load an already posted aggregate. Here we mock state.
        aggregate.markPosted(); 
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_transaction_aggregate_that_violates_balance_validation() {
        // Simulate an account with MAX_VALUE balance, so any deposit overflows
        aggregate = new TransactionAggregate("TXN-OVERFLOW", "ACC-999", BigDecimal.valueOf(Long.MAX_VALUE), "USD");
    }

    @When("the PostDepositCmd command is executed")
    public void the_post_deposit_cmd_command_is_executed() {
        try {
            // Determine parameters based on context setup
            String account = aggregate != null ? aggregate.getAccountNumber() : "ACC-000";
            BigDecimal amount = BigDecimal.valueOf(100.00);
            String currency = "USD";
            
            // Override amount for the "amounts must be > 0" scenario if needed, 
            // but standard Gherkin practice is we reuse the valid command to test the aggregate rejection logic defined in 'Given'.
            if (aggregate != null && aggregate.getAccountNumber().equals("TXN-INVALID")) {
                 amount = BigDecimal.ZERO; // Simulate bad input if the aggregate didn't catch it, but the aggregate handles the check.
            }

            command = new PostDepositCmd(account, amount, currency);
            resultEvents = aggregate.execute(command);
        } catch (IllegalStateException | IllegalArgumentException e) {
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
        assertEquals("TXN-123", event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Verify the exception message matches one of our invariant errors
        assertTrue(
            caughtException.getMessage().contains("greater than zero") ||
            caughtException.getMessage().contains("already posted") ||
            caughtException.getMessage().contains("balance constraint")
        );
    }
}